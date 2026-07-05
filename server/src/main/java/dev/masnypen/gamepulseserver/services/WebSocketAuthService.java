package dev.masnypen.gamepulseserver.services;

import com.google.gson.Gson;
import dev.masnypen.gamepulseserver.configs.AppConfig;
import dev.masnypen.gamepulseserver.models.GPAgent;
import dev.masnypen.gamepulseserver.models.websocket.AuthPacketData;
import dev.masnypen.gamepulseserver.models.websocket.GPAgentSession;
import dev.masnypen.gamepulseserver.repositories.GPAgentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WebSocketAuthService {

    private final GPAgentRepository repo;
    private final SessionRegistry sessionRegistry;
    private final AppConfig config;

    public boolean authenticate(WebSocketSession session, AuthPacketData data) {
        try {

            if (data.getToken().equals(config.getAuth_token())) {
                GPAgent agent = null;

                // Check agent_id is existing or valid
                if (!data.getAgent_id().equals("None")) {
                    try {
                        agent = repo.getGPAgentById(UUID.fromString(data.getAgent_id()));
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

                repo.save(agent);

                sessionRegistry.register(agent.getId(), session);
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
}
