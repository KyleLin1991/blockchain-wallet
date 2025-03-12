package tw.com.kyle.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tw.com.kyle.controller.req.LoginReqDto;
import tw.com.kyle.controller.req.RegisterReqDto;
import tw.com.kyle.controller.resp.LoginRespDto;
import tw.com.kyle.controller.resp.RegisterRespDto;
import tw.com.kyle.dto.PrivilegeDto;
import tw.com.kyle.dto.RoleDto;
import tw.com.kyle.dto.UserTokenDto;
import tw.com.kyle.entity.auth.AccountEntity;
import tw.com.kyle.entity.auth.AccountNRoleEntity;
import tw.com.kyle.entity.auth.RoleEntity;
import tw.com.kyle.entity.auth.RoleNPrivilegeEntity;
import tw.com.kyle.enums.EnableStatus;
import tw.com.kyle.enums.Role;
import tw.com.kyle.repository.auth.AccountNRoleRepository;
import tw.com.kyle.repository.auth.AccountRepository;
import tw.com.kyle.repository.auth.RoleRepository;
import tw.com.kyle.service.AccountService;
import tw.com.kyle.service.BaseService;
import tw.com.kyle.util.JwtTokenUtil;

import java.util.List;

/**
 * @author Kyle
 * @since 2025/3/5
 */
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@CommonsLog
@Service
public class AccountServiceImpl extends BaseService implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountNRoleRepository accountNRoleRepository;
    private final RoleRepository roleRepository;

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public RegisterRespDto register(RegisterReqDto registerReqDto, Authentication authentication) {
        if (log.isDebugEnabled()) {
            JSONObject logParams = new JSONObject();
            logParams.put("accountReqDto", registerReqDto);
            logParams.put("authentication", authentication);

            log.debug(logParams);
        }
        Role role = Role.fromValue(registerReqDto.getType());

        if (Role.ADMIN.equals(role) || Role.BACK_USER.equals(role)) {
            if (super.checkIsAdmin(authentication)) {
                return buildAccount(registerReqDto.getAccount(), registerReqDto.getPassword(), role);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "無權限註冊管理員及後台使用者!!");
            }
        } else {
            return buildAccount(registerReqDto.getAccount(), registerReqDto.getPassword(), role);
        }
    }

    @Override
    public LoginRespDto login(LoginReqDto loginRequest) {
        if (log.isDebugEnabled()) {
            JSONObject logParams = new JSONObject();
            logParams.put("loginRequest.account", loginRequest.getAccount());

            log.debug(logParams);
        }
        // 查詢帳號
        AccountEntity accountEntity = accountRepository.findByAccountAndPasswordAndRoleCodeAndStatus(
                loginRequest.getAccount(),
                super.passwordEncrypt(loginRequest.getPassword()),
                loginRequest.getRole(),
                EnableStatus.ENABLE
        ).orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "帳號或密碼錯誤"));

        // Login Response
        LoginRespDto loginRespDto = new LoginRespDto();

        List<RoleEntity> roleEntities = accountEntity.getRoles().stream()
                .map(AccountNRoleEntity::getRole)
                .toList();

        List<RoleDto> roles = roleEntities.stream()
                .map(roleEntity -> RoleDto.builder()
                        .id(roleEntity.getId())
                        .roleName(roleEntity.getName())
                        .roleCode(roleEntity.getRoleCode().getValue())
                        .build()
                )
                .toList();

        List<PrivilegeDto> privileges = roleEntities.stream()
                .flatMap(role -> role.getRoleNPrivileges().stream())
                .map(RoleNPrivilegeEntity::getPrivilege)
                .map(privilege -> PrivilegeDto.builder()
                        .pid(privilege.getPid())
                        .description(privilege.getDescription())
                        .build())
                .toList();

        loginRespDto.setAccount(accountEntity.getAccount());
        loginRespDto.setRoles(roles);
        loginRespDto.setPrivileges(privileges);

        loginRespDto.setToken(jwtTokenUtil.generateToken(UserTokenDto.builder()
                .sub(accountEntity.getId().toString())
                .account(accountEntity.getAccount())
                .roles(roles)
                .privileges(privileges)
                .build()));

        return loginRespDto;
    }

    private RegisterRespDto buildAccount(String account, String password, Role role) {
        AccountEntity accountEntity = AccountEntity.builder()
                .account(account)
                .password(passwordEncrypt(password))
                .roleCode(role)
                .status(EnableStatus.ENABLE)
                .build();

        accountRepository.save(accountEntity);

        RoleEntity roleEntity = roleRepository.findByRoleCode(role);

        accountNRoleRepository.save(AccountNRoleEntity.builder()
                .account(accountEntity)
                .role(roleEntity)
                .build()
        );

        return RegisterRespDto.builder()
                .account(accountEntity.getAccount())
                .roleName(roleEntity.getName())
                .build();
    }
}
