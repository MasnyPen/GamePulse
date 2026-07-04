package dev.masnypen.gamepulseserver.sockets;

import com.google.gson.Gson;
import dev.masnypen.gamepulseserver.models.PacketType;
import dev.masnypen.gamepulseserver.models.SysStats;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MonitorHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions =
            ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionClosed(WebSocketSession session,
                                      CloseStatus status) {
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
            handleAuthPacket(session, data);
        }

        if (!sessions.contains(session)) {
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

    private void handleAuthPacket(WebSocketSession session, String token) {
        try {
            if (token.equals("token")) {
                sessions.add(session);
                session.sendMessage(new TextMessage("GPULSE_AUTH_ACK;{\"message\":\"valid_token\"}"));

                System.out.println(session.getRemoteAddress() + " connected");
            } else {
                session.sendMessage(new TextMessage("GPULSE_AUTH_ERR;{\"error\":\"invalid_token\"}"));
                session.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleSysStatsPacket(WebSocketSession session, String data) {
        SysStats sysStats = new Gson().fromJson(data, SysStats.class);
        System.out.println(sysStats.toString());
    }
}
