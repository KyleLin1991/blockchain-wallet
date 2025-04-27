package tw.com.blockchain.service;

import org.springframework.security.core.Authentication;
import tw.com.blockchain.controller.req.WithdrawReqDto;
import tw.com.blockchain.controller.resp.WithdrawRespDto;
import tw.com.blockchain.entity.geth.WithdrawEntity;

/**
 * @author Kyle
 * @since 2025/3/24
 */
public interface WithdrawService {

    WithdrawEntity save(WithdrawEntity walletEntity);
    WithdrawEntity findByTransaction(byte[] transactionHash);
    WithdrawRespDto withdraw(WithdrawReqDto withdrawReqDto, Authentication authentication);
}
