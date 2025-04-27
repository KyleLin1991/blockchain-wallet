package tw.com.blockchain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tw.com.blockchain.controller.req.AddUserReqDto;
import tw.com.blockchain.controller.req.UpdateUserReqDto;
import tw.com.blockchain.controller.resp.UserRespDto;
import tw.com.blockchain.dto.RestApiOneResponse;
import tw.com.blockchain.service.UserService;

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

    private final UserService userService;

    // TODO 目前和 SystemUserController api 一樣, 日後再調整相關邏輯及欄位
    @Operation(summary = "新增使用者資訊")
    @PostMapping(value = "/client/user")
    public RestApiOneResponse<String> addUser(@RequestBody @Valid AddUserReqDto addUserReqDto, Authentication authentication) {
        if (log.isDebugEnabled()) {
            log.debug(new JSONObject());
        }

        return super.doGetOneResult(userService.addUser(addUserReqDto, authentication));
    }

    // TODO 目前和 SystemUserController api 一樣, 日後再調整相關邏輯及欄位
    @Operation(summary = "修改使用者資訊")
    @PutMapping(value = "/client/user")
    public RestApiOneResponse<String> updateUser(@RequestBody @Valid UpdateUserReqDto updateUserReqDto) {
        if (log.isDebugEnabled()) {
            log.debug(new JSONObject());
        }

        return super.doGetOneResult(userService.updateUser(updateUserReqDto));
    }

    // TODO 目前和 SystemUserController api 一樣, 日後再調整相關邏輯及欄位
    @Operation(summary = "查詢使用者資訊")
    @GetMapping(value = "/client/user")
    public RestApiOneResponse<UserRespDto> updateUser(Authentication authentication) {
        if (log.isDebugEnabled()) {
            log.debug(new JSONObject());
        }

        return super.doGetOneResult(userService.getUser(authentication));
    }
}
