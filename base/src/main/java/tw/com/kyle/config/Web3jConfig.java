package tw.com.kyle.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import tw.com.kyle.misc.GethProperties;

/**
 * @author Kyle
 * @since 2025/3/12
 */
@Configuration
public class Web3jConfig {

    @Autowired
    private GethProperties gethProperties;

    @Bean
    public Web3j initWeb3j() { return Web3j.build(new HttpService(
            gethProperties.getNodeUrl() + gethProperties.getApiKey())
        );
    }

}
