package tw.com.blockchain.service.websocket;

import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.web3j.protocol.websocket.WebSocketService;
import tw.com.blockchain.misc.GethProperties;

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

    private static final int MAX_RETRY_COUNT = 5;
    private int retryCount = 0;

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
            retryCount = 0;
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
        if (!isShuttingDown && !isConnected && retryCount < MAX_RETRY_COUNT) {
            try {
                scheduler.schedule(this::connectToWebSocket, this.delayMs, TimeUnit.MILLISECONDS);
                retryCount++;
            } catch (RejectedExecutionException e) {
                log.error("Failed to schedule reconnect: {}", e.getMessage());
            }
        } else if (retryCount >= MAX_RETRY_COUNT) {
            log.error("Maximum retry attempts reached. Stopping reconnection attempts.");
        }
    }

    @PreDestroy
    public void destroy() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}
