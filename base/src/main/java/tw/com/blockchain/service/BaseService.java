package tw.com.blockchain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import tw.com.blockchain.dto.UserTokenDto;
import tw.com.blockchain.enums.Role;

/**
 * @author Kyle
 * @since 2025/3/4
 */
public class BaseService {

    @Autowired
    private ObjectMapper objectMapper;

    public String passwordEncrypt(String password) {
        return DigestUtils.sha256Hex(password);
    }

    public UserTokenDto parseUserToken(Authentication authentication) {
        return objectMapper.convertValue(authentication.getCredentials(), UserTokenDto.class);
    }

    public boolean checkIsAdmin(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated() &&
                authentication.getAuthorities().stream()
                        .anyMatch(auth -> Role.ADMIN.getValue().equals(auth.getAuthority()));
    }
}
