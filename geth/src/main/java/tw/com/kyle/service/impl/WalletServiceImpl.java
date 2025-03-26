package tw.com.kyle.service.impl;

import lombok.extern.apachecommons.CommonsLog;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.utils.Numeric;
import tw.com.kyle.controller.req.CreateWalletReqDto;
import tw.com.kyle.controller.req.WithdrawReqDto;
import tw.com.kyle.controller.resp.CreateWalletRespDto;
import tw.com.kyle.controller.resp.WithdrawRespDto;
import tw.com.kyle.dto.UserTokenDto;
import tw.com.kyle.dto.VaultDto;
import tw.com.kyle.entity.auth.AccountEntity;
import tw.com.kyle.entity.geth.TransactionEntity;
import tw.com.kyle.entity.geth.WalletEntity;
import tw.com.kyle.entity.geth.WithdrawEntity;
import tw.com.kyle.enums.TransactionStatus;
import tw.com.kyle.enums.converter.TransactionStatusConverter;
import tw.com.kyle.misc.GethProperties;
import tw.com.kyle.repository.auth.AccountRepository;
import tw.com.kyle.repository.geth.TransactionRepository;
import tw.com.kyle.repository.geth.WalletRepository;
import tw.com.kyle.repository.geth.WithdrawRepository;
import tw.com.kyle.service.BaseService;
import tw.com.kyle.service.TransactionProcessService;
import tw.com.kyle.service.WalletService;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Kyle
 * @since 2025/3/5
 */
@Transactional(rollbackFor = Exception.class)
@CommonsLog
@Service
public class WalletServiceImpl extends BaseService implements WalletService {

    @Value("${spring.vault.key-path}")
    private String vaultKeyPath;

//    @Value("${geth.wallet-path}")
//    private String walletPath;

    private final WalletRepository walletRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final WithdrawRepository withdrawRepository;
    private final TransactionProcessService transactionProcessService;
    private final Web3j httpWeb3j;
    private final VaultTemplate vaultTemplate;
    private final WebSocketService webSocketService;

    @Autowired
    private GethProperties gethProperties;

    public WalletServiceImpl(
            WalletRepository walletRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository,
            WithdrawRepository withdrawRepository,
            TransactionProcessService transactionProcessService,
            @Qualifier("httpWeb3j") Web3j httpWeb3j,
            VaultTemplate vaultTemplate,
            WebSocketService webSocketService) {
        this.walletRepository = walletRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.withdrawRepository = withdrawRepository;
        this.transactionProcessService = transactionProcessService;
        this.httpWeb3j = httpWeb3j;
        this.vaultTemplate = vaultTemplate;
        this.webSocketService = webSocketService;
    }

    @Override
    public CreateWalletRespDto createWallet(CreateWalletReqDto createWalletReqDto, Authentication authentication) {
        if (log.isDebugEnabled()) {
            JSONObject logParams = new JSONObject();
            logParams.put("createWalletReqDto", createWalletReqDto);

            log.debug(logParams);
        }
        UserTokenDto userTokenInfo = super.parseUserToken(authentication);

        AccountEntity accountEntity = accountRepository.findById(UUID.fromString(userTokenInfo.getSub()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "查無此帳號id"));

        if (accountEntity.getUser() != null) {
            try {
                File walletDir = new File(gethProperties.getWalletPath());
                if (!walletDir.exists()) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "錢包資料夾不存在");
                }
                String walletFileName = WalletUtils.generateNewWalletFile(createWalletReqDto.getPassword(), walletDir);
                // 錢包憑證
                Credentials credentials = WalletUtils.loadCredentials(createWalletReqDto.getPassword(), new File(walletDir, walletFileName));

                String walletAddress = credentials.getAddress();
                String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);

                walletRepository.save(WalletEntity.builder()
                        .address(walletAddress)
                        .balance(BigInteger.ZERO)
                        .userEntity(accountEntity.getUser())
                        .crAccount(accountEntity.getId())
                        .build()
                );

                vaultTemplate.write(
                        vaultKeyPath + walletAddress,
                        VaultDto.builder()
                                .walletAddress(walletAddress)
                                .password(createWalletReqDto.getPassword())
                                .privateKey(privateKey)
                                .chainId(httpWeb3j.ethChainId().send().getChainId().toString())
                                .walletDirPath(walletDir.getPath())
                                .crDatetime(Timestamp.from(Instant.now()))
                                .build()
                );

                return CreateWalletRespDto.builder()
                        .address(walletAddress)
                        .balance(BigDecimal.ZERO)
                        .build();
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "尚未新增前台使用者");
        }
    }

    @Override
    public WithdrawRespDto withdraw(WithdrawReqDto withdrawReqDto, Authentication authentication) {
        if (log.isDebugEnabled()) {
            JSONObject logParams = new JSONObject();
            logParams.put("withdrawReqDto", withdrawReqDto);

            log.debug(logParams);
        }
        UserTokenDto userTokenInfo = super.parseUserToken(authentication);

        Optional<WalletEntity> walletOpt = walletRepository.findByAddressAndCrAccount(
                withdrawReqDto.getFrom(),
                UUID.fromString(userTokenInfo.getSub())
        );

        if (walletOpt.isPresent()) {
            try {
                WalletEntity walletEntity = walletOpt.get();
                BigInteger balance = new BigInteger(withdrawReqDto.getBalance());

                if (walletEntity.getBalance().compareTo(balance) <= 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "錢包餘額不得小於等於提領金額");
                }
                // 發送交易
                String txHash = transactionProcessService.sendWithdrawTransaction(
                        withdrawReqDto.getFrom(),
                        withdrawReqDto.getTo(),
                        balance
                );
                log.info("Withdraw transaction sent: " + txHash);


//                    updateWalletData(
//                            walletEntity,
//                            receipt,
//                            withdrawReqDto.getFrom(),
//                            withdrawReqDto.getTo(),
//                            balance
//                    );
//
//                return WithdrawRespDto.builder()
//                        .from(withdrawReqDto.getFrom())
//                        .to(withdrawReqDto.getTo())
//                        .balance(withdrawReqDto.getBalance())
//                        .nonce(receiptFuture.get().getTransactionIndex())
//                        .transactionHash(txHash)
//                        .withdrawTime(new Timestamp(System.currentTimeMillis()))
//                        .build();

                return null;
            } catch (Exception e) {
                log.error("Error processing withdraw from " + withdrawReqDto.getFrom() + " to " + withdrawReqDto.getTo() + ": " + e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "查無此錢包地址 " + withdrawReqDto.getFrom());
        }
    }

    private void updateWalletData(WalletEntity walletEntity, TransactionReceipt receipt, String fromAddress, String toAddress, BigInteger amount) {
        BigInteger balance = walletEntity.getBalance().subtract(amount);
        walletEntity.setBalance(balance);

        TransactionStatusConverter transactionStatusConverter = new TransactionStatusConverter();
        TransactionStatus transactionStatus = transactionStatusConverter.convertToEntityAttribute(receipt.getStatus());

        TransactionEntity transactionEntity = TransactionEntity.builder()
                .hash(receipt.getTransactionHash().getBytes())
                .nonce(receipt.getTransactionIndex())
                .from(fromAddress)
                .to(toAddress)
                .gasPrice(Numeric.toBigInt(receipt.getEffectiveGasPrice()))
                .gas(BigInteger.valueOf(21000))
                .gasUsed(receipt.getGasUsed())
                .status(transactionStatus)
                .build();

        transactionRepository.save(transactionEntity);

        withdrawRepository.save(WithdrawEntity.builder()
                .from(fromAddress)
                .to(toAddress)
                .balance(amount)
                .status(transactionStatus)
                .transaction(transactionEntity)
                .build()
        );
        walletRepository.save(walletEntity);

        log.info("Withdraw confirmed for tx: " + receipt.getTransactionHash());
    }

    public String getBalance(String address) throws Exception {
        return httpWeb3j.ethGetBalance(address, DefaultBlockParameterName.LATEST)
                .send()
                .getBalance()
                .toString();
    }

    @Override
    public Optional<WalletEntity> findByAddressIgnoreCase(String address) {
        return walletRepository.findByAddressIgnoreCase(address);
    }

    @Override
    public WalletEntity save(WalletEntity walletEntity) {
        return walletRepository.save(walletEntity);
    }

    @Override
    public void simulateDisconnect() {
        if (webSocketService != null) {
            log.info("Simulating WebSocket disconnection...");
            webSocketService.close();
        }
    }
}
