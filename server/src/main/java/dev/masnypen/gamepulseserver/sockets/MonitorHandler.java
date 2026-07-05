package dev.masnypen.gamepulseserver.sockets;

import dev.masnypen.gamepulseserver.components.PacketRouter;
import dev.masnypen.gamepulseserver.services.SessionRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@RequiredArgsConstructor
@Component
public class MonitorHandler extends TextWebSocketHandler {

    private final SessionRegistry sessionRegistry;
    private final PacketRouter router;

    @Override
    public void afterConnectionClosed(WebSocketSession session,
                                      CloseStatus status) {
        if (sessionRegistry.isAuthenticated(session)) {
            System.out.println(session.getRemoteAddress() + " disconnected");
        }
        sessionRegistry.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        router.route(session, message.getPayload());
    }
}
