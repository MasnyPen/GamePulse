package dev.masnypen.gamepulseserver.configs;

import dev.masnypen.gamepulseserver.sockets.MonitorHandler;
import dev.masnypen.gamepulseserver.sockets.DashboardLiveHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final MonitorHandler monitorHandler;
    private final DashboardLiveHandler dashboardLiveHandler;

    public WebSocketConfig(MonitorHandler monitorHandler, DashboardLiveHandler dashboardLiveHandler) {
        this.monitorHandler = monitorHandler;
        this.dashboardLiveHandler = dashboardLiveHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(monitorHandler, "/monitor").setAllowedOrigins("*");
        registry.addHandler(dashboardLiveHandler, "/dashboard/live").setAllowedOrigins("*");
    }
}
