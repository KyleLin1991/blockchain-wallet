package tw.com.kyle.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.com.kyle.BaseController;

/**
 * @author Kyle
 * @since 2025/3/3
 */
@CommonsLog
@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "前台使用者 API", description = "User API")
public class UserController extends BaseController {

//    @Operation(summary = "註冊")
//    @PostMapping(value = "/client/user/register")
//    public ResponseEntity<BaseResponseDto<UserRoleResDto>> register(@RequestBody @Valid SystemUserRegisterReqDto registerReq) {
//        if (log.isDebugEnabled()) {
//            log.debug(new JSONObject());
//        }
//
//        return super.doGetDefaultOneResult(systemUserService.register(registerReq));
//    }
}
