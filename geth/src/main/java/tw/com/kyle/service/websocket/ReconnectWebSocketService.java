package tw.com.kyle.service.websocket;

import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Service;
import org.web3j.protocol.websocket.WebSocketService;
import tw.com.kyle.misc.GethProperties;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
            scheduleReconnect();
        }
    }

    @Override
    public void close() {
        super.close();
        isConnected = false;
        log.info("WebSocket closed");
        if (!isShuttingDown) {
            scheduleReconnect();
        }
    }

    private void scheduleReconnect() {
//        if (isShuttingDown || scheduler.isShutdown()) {
//            log.info("Scheduler is shut down or application is closing, skipping reconnect");
//            return;
//        }
//        log.info("Scheduling reconnect with delay: " + delayMs + "ms");
//        try {
//            scheduler.schedule(() -> {
//                if (!isShuttingDown) {
                    log.info("Attempting to reconnect to WebSocket...");
                    connectWithRetry();
//                }
//            }, delayMs, TimeUnit.MILLISECONDS);
//        } catch (RejectedExecutionException e) {
//            log.info("Reconnect task rejected due to scheduler shutdown: " + e.getMessage());
//        }
    }

    // 供外部觸發重連
    public void reconnectOnError() {
        if (isConnected && !isShuttingDown) {
            isConnected = false;
            log.info("Detected disconnection, scheduling reconnect...");
            scheduleReconnect();
        }
    }
//
//    public void shutdown() {
//        isShuttingDown = true;
//
//        if (scheduler instanceof ScheduledThreadPoolExecutor executor) {
//            executor.getQueue().clear();  // 清空任務
//        }
//
//        super.close();
//        log.info("Shutting down ReconnectWebSocketService");
//    }
}
