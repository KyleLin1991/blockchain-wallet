package tw.com.kyle.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.com.kyle.controller.req.LoginReqDto;
import tw.com.kyle.controller.req.RegisterReqDto;
import tw.com.kyle.controller.resp.LoginRespDto;
import tw.com.kyle.controller.resp.RegisterRespDto;
import tw.com.kyle.dto.RestApiOneResponse;
import tw.com.kyle.service.AccountService;

/**
 * @author Kyle
 * @since 2025/3/3
 */
@CommonsLog
@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "帳號 API", description = "Account API")
public class AccountController extends BaseController {

    private final AccountService accountService;

    @Operation(summary = "註冊帳號")
    @PostMapping(value = "/pub/register")
    public RestApiOneResponse<RegisterRespDto> register(@RequestBody @Valid RegisterReqDto registerReqDto, Authentication authentication) {
        if (log.isDebugEnabled()) {
            log.debug(new JSONObject());
        }

        return super.doGetOneResult(accountService.register(registerReqDto, authentication));
    }

    @Operation(summary = "登入")
    @PostMapping(value = "/pub/login")
    public RestApiOneResponse<LoginRespDto> login(@RequestBody @Valid LoginReqDto loginRequest) {
        if (log.isDebugEnabled()) {
            log.debug(new JSONObject());
        }

        return super.doGetOneResult(accountService.login(loginRequest));
    }
}
