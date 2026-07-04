package dev.masnypen.gamepulseserver.configs;

import dev.masnypen.gamepulseserver.sockets.MonitorHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final MonitorHandler monitorHandler;

    public WebSocketConfig(MonitorHandler monitorHandler) {
        this.monitorHandler = monitorHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(monitorHandler, "/monitor").setAllowedOrigins("*");
    }
}
