package dev.masnypen.gamepulseserver.models.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthPacketData {
    private String token;
    private String agent_id;
}
