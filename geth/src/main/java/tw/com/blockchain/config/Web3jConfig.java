package tw.com.blockchain.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import tw.com.blockchain.misc.GethProperties;
import tw.com.blockchain.service.websocket.ReconnectWebSocketService;

/**
 * @author Kyle
 * @since 2025/3/12
 */
@Configuration
public class Web3jConfig {

    @Autowired
    private GethProperties gethProperties;

    @Bean(name = "httpWeb3j")
    public Web3j httpWeb3j() {
        return Web3j.build(new HttpService(gethProperties.getNodeUrl() + gethProperties.getApiKey()));
    }

    @Bean(name = "wsWeb3j")
    public Web3j wsWeb3j(ReconnectWebSocketService reconnectWebSocketService) {
        return Web3j.build(reconnectWebSocketService);
    }
}
