package tw.com.kyle.service;

import org.springframework.security.core.Authentication;
import tw.com.kyle.controller.req.CreateWalletReqDto;
import tw.com.kyle.controller.req.WithdrawReqDto;
import tw.com.kyle.controller.resp.CreateWalletRespDto;
import tw.com.kyle.controller.resp.WithdrawRespDto;
import tw.com.kyle.entity.geth.WalletEntity;

import java.io.IOException;
import java.util.Optional;

/**
 * @author Kyle
 * @since 2025/3/5
 */
public interface WalletService {

    CreateWalletRespDto createWallet(CreateWalletReqDto createWalletReqDto, Authentication authentication);
    WithdrawRespDto withdraw(WithdrawReqDto withdrawReqDto, Authentication authentication);
    String getBalance(String address) throws Exception;
    Optional<WalletEntity> findByAddressIgnoreCase(String address);
    WalletEntity save(WalletEntity walletEntity);
    void simulateDisconnect();
}
