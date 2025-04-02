package tw.com.kyle.service.websocket;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.web3j.protocol.websocket.WebSocketService;
import tw.com.kyle.misc.GethProperties;

import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Kyle
 * @since 2025/3/24
 */
@Slf4j
@Setter
@Getter
@Service
public class ReconnectWebSocketService extends WebSocketService {

    private final String wsUrl;
    private final long delayMs;
    private volatile boolean isConnected = false;
    private volatile boolean isShuttingDown = false;
    private ReconnectListener reconnectListener;

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public ReconnectWebSocketService(GethProperties gethProperties) {
        super(gethProperties.getWsNodeUrl() + gethProperties.getApiKey(), true);
        this.wsUrl = gethProperties.getWsNodeUrl() + gethProperties.getApiKey();
        this.delayMs = gethProperties.getReconnectDelayMs();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        connectToWebSocket();
    }

    @Override
    public void close() {
        isShuttingDown = true;
        isConnected = false;

        log.info("WebSocket connection closed");

        if (isShuttingDown) {
            isShuttingDown = false;
            scheduleReconnect(); // 僅在非主動關閉時重連
        }
    }

    private void connectToWebSocket() {
        if (isShuttingDown) {
            log.info("Service is shutting down, skipping connection attempt");
            return;
        }
        try {
            this.connect();
            isConnected = true;
            isShuttingDown = false;
            log.info("WebSocket connected to {}", this.wsUrl);
            if (reconnectListener != null) {
                reconnectListener.onReconnect();
            }
            eventPublisher.publishEvent(new WebSocketConnectedEvent(this));
        } catch (Exception e) {
            log.error("Connection failed, retrying in {} ", this.delayMs + "ms...");
            isConnected = false;
            scheduleReconnect();
        }
    }

    // 設置重連監聽器
    private void scheduleReconnect() {
        if (!isShuttingDown && !isConnected) {
            try {
                scheduler.schedule(this::connectToWebSocket, this.delayMs, TimeUnit.MILLISECONDS);
            } catch (RejectedExecutionException e) {
                log.error("Failed to schedule reconnect: {}", e.getMessage());
            }
        }
    }
}
