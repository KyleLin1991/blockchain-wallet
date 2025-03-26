//package tw.com.kyle.config;
//
//import jakarta.annotation.PreDestroy;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.web3j.protocol.Web3j;
//import org.web3j.protocol.http.HttpService;
//import org.web3j.protocol.websocket.WebSocketService;
//import tw.com.kyle.misc.GethProperties;
//
//import java.net.ConnectException;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//
///**
// * @author Kyle
// * @since 2025/3/12
// */
//@Configuration
//public class Web3jConfig {
//
//    @Autowired
//    private GethProperties gethProperties;
//
//    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
//
//    @Bean(name = "httpWeb3j")
//    public Web3j httpWeb3j() {
//        return Web3j.build(new HttpService(gethProperties.getNodeUrl() + gethProperties.getApiKey()));
//    }
//
//    @Bean(name = "wsWeb3j")
//    public Web3j wsWeb3j(@Qualifier("webSocketService") WebSocketService webSocketService) {
//        return Web3j.build(webSocketService);
//    }
//
//    @Bean(name = "webSocketService")
//    public WebSocketService webSocketService() throws ConnectException {
//        WebSocketService webSocketService = new WebSocketService(gethProperties.getWsNodeUrl() + gethProperties.getApiKey(), true);
//        webSocketService.connect();
//
//        return webSocketService;
//    }
//
//    @PreDestroy
//    public void shutdown() {
//        scheduler.shutdown();
//    }
//}
