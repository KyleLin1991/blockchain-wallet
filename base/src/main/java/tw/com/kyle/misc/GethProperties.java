package tw.com.kyle.misc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Kyle
 * @since 2025/3/19
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "geth")
public class GethProperties {

    private String nodeUrl;
    private String apiKey;
    private String walletPath;
}
