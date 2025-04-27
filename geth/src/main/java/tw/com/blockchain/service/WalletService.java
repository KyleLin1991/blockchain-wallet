package tw.com.blockchain.service;

import org.springframework.security.core.Authentication;
import tw.com.blockchain.controller.req.CreateWalletReqDto;
import tw.com.blockchain.controller.resp.CreateWalletRespDto;
import tw.com.blockchain.entity.geth.WalletEntity;

import java.util.Optional;

/**
 * @author Kyle
 * @since 2025/3/5
 */
public interface WalletService {

    CreateWalletRespDto createWallet(CreateWalletReqDto createWalletReqDto, Authentication authentication);
    String getBalance(String address) throws Exception;
    Optional<WalletEntity> findByAddressIgnoreCase(String address);
    WalletEntity save(WalletEntity walletEntity);
    void simulateDisconnect();
}
