package tw.com.blockchain.service.impl;

import lombok.extern.apachecommons.CommonsLog;
import org.json.JSONObject;
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
import tw.com.blockchain.controller.req.CreateWalletReqDto;
import tw.com.blockchain.controller.resp.CreateWalletRespDto;
import tw.com.blockchain.dto.UserTokenDto;
import tw.com.blockchain.dto.VaultDto;
import tw.com.blockchain.entity.auth.AccountEntity;
import tw.com.blockchain.entity.geth.WalletEntity;
import tw.com.blockchain.repository.auth.AccountRepository;
import tw.com.blockchain.repository.geth.WalletRepository;
import tw.com.blockchain.service.BaseService;
import tw.com.blockchain.service.WalletService;
import tw.com.blockchain.service.websocket.ReconnectWebSocketService;

import java.io.File;
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

    @Value("${geth.wallet-path}")
    private String walletPath;

    private final WalletRepository walletRepository;
    private final AccountRepository accountRepository;
    private final Web3j httpWeb3j;
    private final VaultTemplate vaultTemplate;
    private final ReconnectWebSocketService reconnectWebSocketService;

    public WalletServiceImpl(
            WalletRepository walletRepository,
            AccountRepository accountRepository,
            @Qualifier("httpWeb3j") Web3j httpWeb3j,
            VaultTemplate vaultTemplate,
            ReconnectWebSocketService reconnectWebSocketService) {
        this.walletRepository = walletRepository;
        this.accountRepository = accountRepository;
        this.httpWeb3j = httpWeb3j;
        this.vaultTemplate = vaultTemplate;
        this.reconnectWebSocketService = reconnectWebSocketService;
    }

    @Override
    public WalletEntity save(WalletEntity walletEntity) {
        return walletRepository.save(walletEntity);
    }

    @Override
    public Optional<WalletEntity> findByAddressIgnoreCase(String address) {
        return walletRepository.findByAddressIgnoreCase(address);
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
                File walletDir = new File(walletPath);
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

    public String getBalance(String address) throws Exception {
        return httpWeb3j.ethGetBalance(address, DefaultBlockParameterName.LATEST)
                .send()
                .getBalance()
                .toString();
    }

    @Override
    public void simulateDisconnect() {
        if (reconnectWebSocketService != null) {
            log.info("Simulating WebSocket disconnection...");
            reconnectWebSocketService.close();
        }
    }
}
