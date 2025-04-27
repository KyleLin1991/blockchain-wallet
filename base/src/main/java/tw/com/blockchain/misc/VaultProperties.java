package tw.com.blockchain.misc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Kyle
 * @since 2025/3/13
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.vault")
public class VaultProperties {

    private String scheme;
    private String host;
    private String port;
    private String token;
}
