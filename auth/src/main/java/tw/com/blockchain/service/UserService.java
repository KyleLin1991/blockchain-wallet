package tw.com.blockchain.service;

import org.springframework.security.core.Authentication;
import tw.com.blockchain.controller.req.AddUserReqDto;
import tw.com.blockchain.controller.req.UpdateUserReqDto;
import tw.com.blockchain.controller.resp.UserRespDto;

/**
 * @author Kyle
 * @since 2025/3/5
 */
public interface UserService {

    String addUser(AddUserReqDto addUserReqDto, Authentication authentication);
    String updateUser(UpdateUserReqDto updateUserReqDto);
    UserRespDto getUser(Authentication authentication);
}
