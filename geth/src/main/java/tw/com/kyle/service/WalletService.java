package tw.com.kyle.service;

import org.springframework.security.core.Authentication;
import tw.com.kyle.controller.req.CreateWalletReqDto;
import tw.com.kyle.controller.resp.CreateWalletRespDto;

import java.io.IOException;

/**
 * @author Kyle
 * @since 2025/3/5
 */
public interface WalletService {

    CreateWalletRespDto createWallet(CreateWalletReqDto createWalletReqDto, Authentication authentication);
}
