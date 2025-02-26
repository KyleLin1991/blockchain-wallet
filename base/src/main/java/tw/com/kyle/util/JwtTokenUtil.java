package tw.com.kyle.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tw.com.kyle.dto.UserTokenDto;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * @author Kyle
 * @since 2025/2/26
 */
@Slf4j
@Data
@Component
public class JwtTokenUtil {

    @Value("${jwt.expiration.login}")
    private int loginLifeTime;

    @Value("${jwt.expiration.resetPassword}")
    private int resetPasswordLifeTime;

    @Value("${jwt.publicKeyPem}")
    private String b64PublicKey;

    @Value("${jwt.privateKeyPem}")
    private String b64PrivateKey;

    private Key jwtTokenKey;

    // 公鑰
    private PublicKey publicKey;

    // 私鑰
    private PrivateKey privateKey;

    @PostConstruct
    public void init() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        this.publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(b64PublicKey)));
        this.privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(b64PrivateKey)));
    }

    /**
     * 非對稱金鑰(登入) 10小時過期
     * @param info Token Info
     * @return String
     */
    public String generateToken(UserTokenDto info) {
        final long now = System.currentTimeMillis();

        // 設定 JWT Claims
        Claims claims = Jwts.claims()
                .add("account", info.getAccount())
                .add("roles", info.getRoles())
                .build();

        return Jwts.builder()
                .claims(claims)
                .subject(info.getSub())
                .issuedAt(new Date(now))
                .expiration(new Date(now + loginLifeTime * 1000L))
                .signWith(privateKey)
                .compact();
    }

    public Optional<Map<String, Object>> parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Optional.of(claims);
        } catch (Exception e) {
            log.error("Parse JWT token fail, cause: {}", e.getMessage());
            return Optional.empty();
        }
    }

}
