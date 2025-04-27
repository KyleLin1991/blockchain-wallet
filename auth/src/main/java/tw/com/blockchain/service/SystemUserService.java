package tw.com.blockchain.service;

import org.springframework.security.core.Authentication;
import tw.com.blockchain.controller.req.AddSystemUserReqDto;
import tw.com.blockchain.controller.req.UpdateSystemUserReqDto;
import tw.com.blockchain.controller.resp.SystemUserRespDto;
import tw.com.blockchain.controller.resp.UserRespDto;

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
