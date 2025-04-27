package tw.com.blockchain.service;

import org.springframework.security.core.Authentication;
import tw.com.blockchain.controller.req.LoginReqDto;
import tw.com.blockchain.controller.req.RegisterReqDto;
import tw.com.blockchain.controller.resp.LoginRespDto;
import tw.com.blockchain.controller.resp.RegisterRespDto;

/**
 * @author Kyle
 * @since 2025/3/5
 */
public interface AccountService {

    RegisterRespDto register(RegisterReqDto registerReqDto, Authentication authentication);
    LoginRespDto login(LoginReqDto loginRequest);
}
