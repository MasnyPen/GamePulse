package dev.masnypen.gamepulseserver.sockets;

import dev.masnypen.gamepulseserver.components.PacketRouter;
import dev.masnypen.gamepulseserver.models.websocket.GPAgentSession;
import dev.masnypen.gamepulseserver.services.SessionRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Component
public class MonitorHandler extends TextWebSocketHandler {

    private final SessionRegistry sessionRegistry;
    private final PacketRouter router;

    private final Set<GPAgentSession> sessions =
            ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionClosed(WebSocketSession session,
                                      CloseStatus status) {
        if (sessionRegistry.isAuthenticated(session)) {
            System.out.println(session.getRemoteAddress() + " disconnected");
        }
        sessions.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        router.route(session, message.getPayload());
    }
}
