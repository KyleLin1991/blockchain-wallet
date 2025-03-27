package tw.com.kyle.service.websocket;

import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Service;
import org.web3j.protocol.websocket.WebSocketService;
import tw.com.kyle.misc.GethProperties;

import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Kyle
 * @since 2025/3/24
 */
@Setter
@Service
@CommonsLog
public class ReconnectWebSocketService extends WebSocketService {

    private final String wsUrl;
    private final long delayMs;
    private final ScheduledExecutorService scheduler;
    private volatile boolean isConnected = false;
    private volatile boolean isShuttingDown = false;
    private ReconnectListener reconnectListener;

    public ReconnectWebSocketService(GethProperties gethProperties, ScheduledExecutorService scheduler) {
        super(gethProperties.getWsNodeUrl() + gethProperties.getApiKey(), true);
        this.wsUrl = gethProperties.getWsNodeUrl() + gethProperties.getApiKey();
        this.delayMs = gethProperties.getReconnectDelayMs();
        this.scheduler = scheduler;
        connectWithRetry(); // 初始化時連接
    }

    @Override
    public void connect() {
        connectWithRetry();
    }

    private void connectWithRetry() {
        if (isShuttingDown) {
            log.info("Application is shutting down, skipping reconnect attempt");
            return;
        }
        try {
            super.connect();
            isConnected = true;
            log.info("WebSocket connected to: " + wsUrl);
            if (reconnectListener != null) {
                reconnectListener.onReconnect(); // 重連成功後調用回調
            }
        } catch (Exception e) {
            isConnected = false;
            log.error("Failed to connect to WebSocket: " + e.getMessage());
            connectWithRetry();
        }
    }

//    @Override
//    public void close() {
//        super.close();
//        isConnected = false;
//        log.info("WebSocket closed");
//        if (!isShuttingDown) {
//            connectWithRetry();
//        }
//    }

    // 供外部觸發重連
    public void reconnectOnError() {
        if (isConnected && !isShuttingDown) {
            isConnected = false;
            log.info("Detected disconnection, scheduling reconnect...");
            connectWithRetry();
        }
    }
}
