package dev.masnypen.gamepulseserver.components;

import com.google.gson.Gson;
import dev.masnypen.gamepulseserver.models.sysstats.SysStats;
import dev.masnypen.gamepulseserver.models.agents.AuthPacketData;
import dev.masnypen.gamepulseserver.models.agents.PacketType;
import dev.masnypen.gamepulseserver.services.SessionRegistry;
import dev.masnypen.gamepulseserver.services.SysStatsService;
import dev.masnypen.gamepulseserver.services.WebSocketAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PacketRouter {

    private final WebSocketAuthService authService;
    private final SysStatsService sysStatsService;

    private final SessionRegistry registry;

    private final Gson gson = new Gson();

    public void route(WebSocketSession session, String raw) {
        String[] payload = raw.split(";");

        String packetType_raw = payload[0];
        PacketType packetType = PacketType.valueOf(packetType_raw);

        String data = payload[1];

        if (packetType == PacketType.GPULSE_AUTH) {
            AuthPacketData authPacketData = gson.fromJson(data, AuthPacketData.class);

            if (!authService.authenticate(session, authPacketData)) {
                return;
            }
        }

        if (!registry.isAuthenticated(session)) {
            try {
                session.sendMessage(new TextMessage(PacketType.GPULSE_DENY + ";{\"error\":\"not_auth\"}"));

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        switch (packetType) {
            case PacketType.GPULSE_SYSSTATS:
                SysStats sysStats = gson.fromJson(data, SysStats.class);
                sysStatsService.handle(session, sysStats);
                break;
            default:
                try {
                    session.sendMessage(new TextMessage(PacketType.GPULSE_DENY + ";{\"error\":\"invalid_packet_type\"}"));
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
        }
    }

}
