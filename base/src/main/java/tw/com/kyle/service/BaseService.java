package tw.com.kyle.service;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author Kyle
 * @since 2025/3/4
 */
public class BaseService {

    public String passwordEncrypt(String password) {
        return DigestUtils.sha256Hex(password);
    }
}
