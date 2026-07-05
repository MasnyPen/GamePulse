package dev.masnypen.gamepulseserver.models.websocket;

import dev.masnypen.gamepulseserver.models.GPAgent;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

@Data
@AllArgsConstructor
public class GPAgentSession {
    private WebSocketSession session;
    private UUID agentId;
    private final long connectedAt;
}
