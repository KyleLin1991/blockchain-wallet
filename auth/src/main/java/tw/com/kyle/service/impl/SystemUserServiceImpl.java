package tw.com.kyle.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tw.com.kyle.controller.req.AddSystemUserReqDto;
import tw.com.kyle.controller.req.UpdateSystemUserReqDto;
import tw.com.kyle.controller.resp.SystemUserRespDto;
import tw.com.kyle.controller.resp.UserRespDto;
import tw.com.kyle.dto.UserTokenDto;
import tw.com.kyle.entity.AccountEntity;
import tw.com.kyle.entity.SystemUserEntity;
import tw.com.kyle.enums.EnableStatus;
import tw.com.kyle.enums.Role;
import tw.com.kyle.enums.UserResultMsg;
import tw.com.kyle.repository.AccountRepository;
import tw.com.kyle.repository.SystemUserRepository;
import tw.com.kyle.service.BaseService;
import tw.com.kyle.service.SystemUserService;
import tw.com.kyle.util.PojoUtil;

import java.util.List;
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
public class SystemUserServiceImpl extends BaseService implements SystemUserService {

    private final AccountRepository accountRepository;
    private final SystemUserRepository systemUserRepository;

    @Override
    public String addSystemUser(AddSystemUserReqDto addSystemUserReqDto, Authentication authentication) {
        if (log.isDebugEnabled()) {
            JSONObject logParams = new JSONObject();
            logParams.put("addSystemUserReqDto", addSystemUserReqDto);
            logParams.put("authentication", authentication);

            log.debug(logParams);
        }
        UserTokenDto userTokenInfo = super.parseUserToken(authentication);

        AccountEntity accountEntity = accountRepository.findById(UUID.fromString(userTokenInfo.getSub()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "查無此帳號id"));

        Optional<SystemUserEntity> systemUserOpt = systemUserRepository.findByAccountId(accountEntity.getId());

        if (systemUserOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "使用者資料已存在");
        } else {
            systemUserRepository.save(SystemUserEntity.builder()
                    .name(addSystemUserReqDto.getName())
                    .email(addSystemUserReqDto.getEmail())
                    .phoneNumber(addSystemUserReqDto.getPhoneNumber())
                    .address(addSystemUserReqDto.getAddress())
                    .enableStatus(EnableStatus.ENABLE)
                    .crAccount(accountEntity.getId())
                    .account(accountEntity)
                    .build());
        }

        return UserResultMsg.ADD_SUCCESS.getValue();
    }

    @Override
    public String updateSystemUser(UpdateSystemUserReqDto updateSystemUserReqDto) {
        if (log.isDebugEnabled()) {
            JSONObject logParams = new JSONObject();
            logParams.put("updateSystemUserReqDto", updateSystemUserReqDto);

            log.debug(logParams);
        }
        SystemUserEntity systemUserEntity = systemUserRepository.findById(updateSystemUserReqDto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "查無此使用者id"));

        PojoUtil.copyProperties(updateSystemUserReqDto, systemUserEntity, true);

        systemUserRepository.save(systemUserEntity);

        return UserResultMsg.UPDATE_SUCCESS.getValue();
    }

    @Override
    public SystemUserRespDto getSystemUser(Authentication authentication) {
        if (log.isDebugEnabled()) {
            JSONObject logParams = new JSONObject();
            logParams.put("authentication", authentication);

            log.debug(logParams);
        }
        UserTokenDto userTokenInfo = super.parseUserToken(authentication);

        AccountEntity accountEntity = accountRepository.findById(UUID.fromString(userTokenInfo.getSub()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "查無此帳號id"));

        SystemUserEntity systemUserEntity = systemUserRepository.findByAccountId(accountEntity.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "查無此使用者 accountId"));

        return this.buildSystemUser(systemUserEntity);
    }

    @Override
    public List<SystemUserRespDto> getSystemUsers(Authentication authentication) {
        if (log.isDebugEnabled()) {
            JSONObject logParams = new JSONObject();
            logParams.put("authentication", authentication);

            log.debug(logParams);
        }
        if (super.checkIsAdmin(authentication)) {
            List<SystemUserEntity> systemUserEntityList = systemUserRepository.findAll();

            return systemUserEntityList.stream().map(this::buildSystemUser).toList();
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "無權限查詢");
        }
    }

    @Override
    public List<UserRespDto> getUsers() {
        return accountRepository.findByRoleCode(Role.FRONT_USER).stream()
                .map(accountEntity -> UserRespDto.builder()
                        .firstName(accountEntity.getUser().getFirstName())
                        .lastName(accountEntity.getUser().getLastName())
                        .email(accountEntity.getUser().getEmail())
                        .phoneNumber(accountEntity.getUser().getPhoneNumber())
                        .address(accountEntity.getUser().getAddress())
                        .crDateTime(accountEntity.getUser().getCrDatetime())
                        .build())
                .toList();
    }

    private SystemUserRespDto buildSystemUser(SystemUserEntity systemUserEntity) {
        return SystemUserRespDto.builder()
                .id(systemUserEntity.getId())
                .name(systemUserEntity.getName())
                .email(systemUserEntity.getEmail())
                .phoneNumber(systemUserEntity.getPhoneNumber())
                .address(systemUserEntity.getAddress())
                .crDateTime(systemUserEntity.getCrDatetime())
                .build();
    }
}
