package tw.com.kyle.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.json.JSONObject;
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
import tw.com.kyle.controller.req.CreateWalletReqDto;
import tw.com.kyle.controller.resp.CreateWalletRespDto;
import tw.com.kyle.dto.UserTokenDto;
import tw.com.kyle.dto.VaultReq;
import tw.com.kyle.entity.auth.AccountEntity;
import tw.com.kyle.entity.geth.WalletEntity;
import tw.com.kyle.repository.auth.AccountRepository;
import tw.com.kyle.repository.geth.WalletRepository;
import tw.com.kyle.service.BaseService;
import tw.com.kyle.service.WalletService;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

/**
 * @author Kyle
 * @since 2025/3/5
 */
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@CommonsLog
@Service
public class WalletServiceImpl extends BaseService implements WalletService {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${geth.wallet-path}")
    private String walletPath;

    private final WalletRepository walletRepository;
    private final AccountRepository accountRepository;

    private final Web3j web3j;
    private final VaultTemplate vaultTemplate;

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
                        "secret/" + applicationName + "/wallets/" + walletAddress,
                        VaultReq.builder()
                                .walletAddress(walletAddress)
                                .password(createWalletReqDto.getPassword())
                                .privateKey(privateKey)
                                .chainId(web3j.ethChainId().send().getChainId().toString())
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

//    public String getBalance(String address) throws Exception {
//        return web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST)
//                .send()
//                .getBalance()
//                .toString();
//    }
}
