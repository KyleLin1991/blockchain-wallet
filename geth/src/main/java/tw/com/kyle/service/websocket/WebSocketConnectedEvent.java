package tw.com.kyle.service.websocket;

import org.springframework.context.ApplicationEvent;

/**
 * @author Kyle
 * @since 2025/4/1
 */
public class WebSocketConnectedEvent extends ApplicationEvent {

    public WebSocketConnectedEvent(Object source) {
        super(source);
    }
}
