package tw.com.kyle.service;

import org.springframework.security.core.Authentication;
import tw.com.kyle.controller.req.AddSystemUserReqDto;
import tw.com.kyle.controller.req.UpdateSystemUserReqDto;
import tw.com.kyle.controller.resp.SystemUserRespDto;
import tw.com.kyle.controller.resp.UserRespDto;

import java.util.List;

/**
 * @author Kyle
 * @since 2025/3/5
 */
public interface SystemUserService {

    String addSystemUser(AddSystemUserReqDto addSystemUserReqDto, Authentication authentication);
    String updateSystemUser(UpdateSystemUserReqDto updateSystemUserReqDto);
    SystemUserRespDto getSystemUser(Authentication authentication);
    List<SystemUserRespDto> getSystemUsers(Authentication authentication);
    List<UserRespDto> getUsers();
}
