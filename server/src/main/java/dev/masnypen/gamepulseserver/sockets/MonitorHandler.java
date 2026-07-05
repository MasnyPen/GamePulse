package dev.masnypen.gamepulseserver.sockets;

import com.google.gson.Gson;
import dev.masnypen.gamepulseserver.models.GPAgent;
import dev.masnypen.gamepulseserver.models.websocket.AuthPacketData;
import dev.masnypen.gamepulseserver.models.websocket.GPAgentSession;
import dev.masnypen.gamepulseserver.models.websocket.PacketType;
import dev.masnypen.gamepulseserver.models.sysstats.SysStats;
import dev.masnypen.gamepulseserver.repositories.GPAgentRepository;
import dev.masnypen.gamepulseserver.services.SysStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Component
public class MonitorHandler extends TextWebSocketHandler {

    private final SysStatsService sysStatsService;
    private final GPAgentRepository gpAgentRepository;
    private final Gson gson = new Gson();

    @Value("${app.auth_token}")
    private String authToken;

    private final Set<GPAgentSession> sessions =
            ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionClosed(WebSocketSession session,
                                      CloseStatus status) {
        if (containsWebsocketSession(session)) {
            System.out.println(session.getRemoteAddress() + " disconnected");
        }
        sessions.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session,
                                     TextMessage message) {
        String[] payload = message.getPayload().split(";");

        String packetType_raw = payload[0];
        PacketType packetType = PacketType.valueOf(packetType_raw);

        String data = payload[1];

        if (packetType == PacketType.GPULSE_AUTH) {
            if (!handleAuthPacket(session, data)) {
                return;
            }
        }

        if (!containsWebsocketSession(session)) {
            try {
                session.sendMessage(new TextMessage(PacketType.GPULSE_DENY + ";{\"error\":\"not_auth\"}"));

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        switch (packetType) {
            case PacketType.GPULSE_SYSSTATS:
                handleSysStatsPacket(session, data);
                break;
        }

    }

    private boolean containsWebsocketSession(WebSocketSession ws_session) {
        for (GPAgentSession session : sessions) {
            if (ws_session.equals(session.getSession())) {
                return true;
            }
        }

        return false;
    }

    private UUID getAgentIdBySession(WebSocketSession ws_session) {
        for (GPAgentSession session : sessions) {
            if (ws_session.equals(session.getSession())) {
                return session.getAgentId();
            }
        }

        return null;
    }

    private boolean handleAuthPacket(WebSocketSession session, String data) {
        try {
            AuthPacketData authPacketData = gson.fromJson(data, AuthPacketData.class);

            if (authPacketData.getToken().equals(authToken)) {
                GPAgent agent = null;

                if (!authPacketData.getAgent_id().equals("None")) {
                    try {
                        agent = gpAgentRepository.getGPAgentById(UUID.fromString(authPacketData.getAgent_id()));
                    } catch (Exception e) {
                        session.sendMessage(new TextMessage("GPULSE_AUTH_ERR;{\"error\":\"invalid_agent_id\"}"));
                        session.close();
                        return false;
                    }
                    if (agent == null) {
                        session.sendMessage(new TextMessage("GPULSE_AUTH_ERR;{\"error\":\"invalid_agent_id\"}"));
                        session.close();
                        return false;
                    }
                }

                String lastIp = session.getRemoteAddress().getAddress().getHostAddress();
                Long lastAuth = System.currentTimeMillis();

                if (agent == null) {
                    agent = new GPAgent();
                }
                agent.setHostname(session.getRemoteAddress().getHostName());
                agent.setLastIp(lastIp);
                agent.setLastAuth(lastAuth);

                gpAgentRepository.save(agent);

                sessions.add(new GPAgentSession(session, agent.getId()));
                session.sendMessage(new TextMessage("GPULSE_AUTH_ACK;{\"agent_id\":\"" + agent.getId() + "\"}"));

                System.out.println(session.getRemoteAddress() + " connected");
                return true;
            } else {
                session.sendMessage(new TextMessage("GPULSE_AUTH_ERR;{\"error\":\"invalid_token\"}"));
                session.close();
                return false;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private void handleSysStatsPacket(WebSocketSession session, String data) {
        SysStats sysStats = gson.fromJson(data, SysStats.class);
        sysStatsService.save(sysStats, getAgentIdBySession(session));
    }
}
