package tw.com.blockchain.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tw.com.blockchain.controller.req.AddSystemUserReqDto;
import tw.com.blockchain.controller.req.UpdateSystemUserReqDto;
import tw.com.blockchain.controller.resp.SystemUserRespDto;
import tw.com.blockchain.controller.resp.UserRespDto;
import tw.com.blockchain.dto.RestApiListResponse;
import tw.com.blockchain.dto.RestApiOneResponse;
import tw.com.blockchain.service.SystemUserService;

/**
 * @author Kyle
 * @since 2025/3/3
 */
@CommonsLog
@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "後台使用者 API", description = "System User API")
public class SystemUserController extends BaseController {

    private final SystemUserService systemUserService;

    // TODO 目前和 UserController api 一樣, 日後再調整相關邏輯及欄位
    @Operation(summary = "新增使用者資訊")
    @PostMapping(value = "/mgmt/systemUser")
    public RestApiOneResponse<String> addSystemUser(@RequestBody @Valid AddSystemUserReqDto addSystemUserReqDto, Authentication authentication) {
        if (log.isDebugEnabled()) {
            log.debug(new JSONObject());
        }

        return super.doGetOneResult(systemUserService.addSystemUser(addSystemUserReqDto, authentication));
    }

    // TODO 目前和 UserController api 一樣, 日後再調整相關邏輯及欄位
    @Operation(summary = "修改使用者資訊")
    @PutMapping(value = "/mgmt/systemUser")
    public RestApiOneResponse<String> updateUser(@RequestBody @Valid UpdateSystemUserReqDto updateSystemUserReqDto) {
        if (log.isDebugEnabled()) {
            log.debug(new JSONObject());
        }

        return super.doGetOneResult(systemUserService.updateSystemUser(updateSystemUserReqDto));
    }

    // TODO 目前和 UserController api 一樣, 日後再調整相關邏輯及欄位
    @Operation(summary = "查詢後台單一使用者資訊")
    @GetMapping(value = "/mgmt/systemUser")
    public RestApiOneResponse<SystemUserRespDto> getSystemUser(Authentication authentication) {
        if (log.isDebugEnabled()) {
            log.debug(new JSONObject());
        }

        return super.doGetOneResult(systemUserService.getSystemUser(authentication));
    }

    @Operation(summary = "查詢後台所有使用者資訊")
    @GetMapping(value = "/mgmt/systemUsers")
    public RestApiListResponse<SystemUserRespDto> getSystemUsers(Authentication authentication) {
        if (log.isDebugEnabled()) {
            log.debug(new JSONObject());
        }

        return super.doGetListResult(systemUserService.getSystemUsers(authentication));
    }

    @Operation(summary = "查詢前台所有使用者資訊")
    @GetMapping(value = "/mgmt/users")
    public RestApiListResponse<UserRespDto> getUsers() {
        if (log.isDebugEnabled()) {
            log.debug(new JSONObject());
        }

        return super.doGetListResult(systemUserService.getUsers());
    }


}
