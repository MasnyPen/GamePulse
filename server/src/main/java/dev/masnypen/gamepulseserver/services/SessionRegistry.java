package dev.masnypen.gamepulseserver.services;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionRegistry {

    private final Map<UUID, WebSocketSession> sessionsByAgent = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, UUID> agentBySession = new ConcurrentHashMap<>();

    public void register(UUID agentId, WebSocketSession session) {
        sessionsByAgent.put(agentId, session);
        agentBySession.put(session, agentId);
    }

    public void remove(WebSocketSession session) {
        UUID agentId = agentBySession.remove(session);
        if (agentId != null) {
            sessionsByAgent.remove(agentId);
        }
    }

    public boolean isAuthenticated(WebSocketSession session) {
        return agentBySession.containsKey(session);
    }

    public UUID getAgentId(WebSocketSession session) {
        return agentBySession.get(session);
    }

    public WebSocketSession getSession(UUID agentId) {
        return sessionsByAgent.get(agentId);
    }
}