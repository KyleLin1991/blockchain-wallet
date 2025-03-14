package tw.com.kyle.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.net.ConnectException;

/**
 * @author Kyle
 * @since 2025/3/12
 */
@Configuration
public class Web3jConfig {

    @Value("${geth.node-url}")
    private String nodeUrl;

    @Bean
    public Web3j initWeb3j() throws ConnectException { return Web3j.build(new HttpService(nodeUrl)); }

}
