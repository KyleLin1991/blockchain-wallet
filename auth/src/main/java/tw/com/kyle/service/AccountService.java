package tw.com.kyle.service;

import org.springframework.security.core.Authentication;
import tw.com.kyle.controller.req.LoginReqDto;
import tw.com.kyle.controller.req.RegisterReqDto;
import tw.com.kyle.controller.resp.LoginRespDto;
import tw.com.kyle.controller.resp.RegisterRespDto;

/**
 * @author Kyle
 * @since 2025/3/5
 */
public interface AccountService {

    RegisterRespDto register(RegisterReqDto registerReqDto, Authentication authentication);
    LoginRespDto login(LoginReqDto loginRequest);
}
