package tw.com.kyle.service;

import org.springframework.security.core.Authentication;
import tw.com.kyle.controller.req.AddUserReqDto;
import tw.com.kyle.controller.req.UpdateUserReqDto;
import tw.com.kyle.controller.resp.UserRespDto;

import java.util.UUID;

/**
 * @author Kyle
 * @since 2025/3/5
 */
public interface UserService {

    String addUser(AddUserReqDto addUserReqDto, Authentication authentication);
    String updateUser(UpdateUserReqDto updateUserReqDto);
    UserRespDto getUser(Authentication authentication);
}
