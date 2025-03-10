package tw.com.kyle.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tw.com.kyle.controller.req.AddUserReqDto;
import tw.com.kyle.controller.req.UpdateUserReqDto;
import tw.com.kyle.controller.resp.UserRespDto;
import tw.com.kyle.dto.UserTokenDto;
import tw.com.kyle.entity.AccountEntity;
import tw.com.kyle.entity.UserEntity;
import tw.com.kyle.enums.EnableStatus;
import tw.com.kyle.enums.UserResultMsg;
import tw.com.kyle.repository.AccountRepository;
import tw.com.kyle.repository.UserRepository;
import tw.com.kyle.service.BaseService;
import tw.com.kyle.service.UserService;
import tw.com.kyle.util.PojoUtil;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Kyle
 * @since 2025/3/5
 */
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@CommonsLog
@Service
public class UserServiceImpl extends BaseService implements UserService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Override
    public String addUser(AddUserReqDto addUserReqDto, Authentication authentication) {
        if (log.isDebugEnabled()) {
            JSONObject logParams = new JSONObject();
            logParams.put("addUserReqDto", addUserReqDto);

            log.debug(logParams);
        }
        UserTokenDto userTokenInfo = super.parseUserToken(authentication);

        AccountEntity accountEntity = accountRepository.findById(UUID.fromString(userTokenInfo.getSub()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "查無此帳號id"));

        Optional<UserEntity> userOpt = userRepository.findByAccountId(accountEntity.getId());

        if (userOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "使用者資料已存在");
        } else {
            userRepository.save(UserEntity.builder()
                    .firstName(addUserReqDto.getFirstName())
                    .lastName(addUserReqDto.getLastName())
                    .email(addUserReqDto.getEmail())
                    .phoneNumber(addUserReqDto.getPhoneNumber())
                    .address(addUserReqDto.getAddress())
                    .enableStatus(EnableStatus.ENABLE)
                    .crAccount(accountEntity.getId())
                    .account(accountEntity)
                    .build());
        }

        return UserResultMsg.ADD_SUCCESS.getValue();
    }

    @Override
    public String updateUser(UpdateUserReqDto updateUserReqDto) {
        if (log.isDebugEnabled()) {
            JSONObject logParams = new JSONObject();
            logParams.put("updateUserReqDto", updateUserReqDto);

            log.debug(logParams);
        }
        UserEntity userEntity = userRepository.findById(updateUserReqDto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "查無此使用者id"));

        PojoUtil.copyProperties(updateUserReqDto, userEntity, true);

        userRepository.save(userEntity);

        return UserResultMsg.UPDATE_SUCCESS.getValue();
    }

    @Override
    public UserRespDto getUser(Authentication authentication) {
        if (log.isDebugEnabled()) {
            JSONObject logParams = new JSONObject();
            logParams.put("authentication", authentication);

            log.debug(logParams);
        }
        UserTokenDto userTokenInfo = super.parseUserToken(authentication);

        AccountEntity accountEntity = accountRepository.findById(UUID.fromString(userTokenInfo.getSub()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "查無此帳號id"));

        UserEntity userEntity = userRepository.findByAccountId(accountEntity.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "查無此使用者 accountId"));

        return UserRespDto.builder()
                .id(userEntity.getId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .email(userEntity.getEmail())
                .phoneNumber(userEntity.getPhoneNumber())
                .address(userEntity.getAddress())
                .crDateTime(userEntity.getCrDatetime())
                .build();
    }
}
