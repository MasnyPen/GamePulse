package dev.masnypen.gamepulseserver.models.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthPacketData {
    private String token;
    private String agent_id;
}
