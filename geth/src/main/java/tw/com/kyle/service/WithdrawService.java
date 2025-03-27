package tw.com.kyle.service;

import org.springframework.security.core.Authentication;
import tw.com.kyle.controller.req.WithdrawReqDto;
import tw.com.kyle.controller.resp.WithdrawRespDto;
import tw.com.kyle.entity.geth.WithdrawEntity;

/**
 * @author Kyle
 * @since 2025/3/24
 */
public interface WithdrawService {

    WithdrawEntity save(WithdrawEntity walletEntity);
    WithdrawEntity findByTransaction(byte[] transactionHash);
    WithdrawRespDto withdraw(WithdrawReqDto withdrawReqDto, Authentication authentication);
}
