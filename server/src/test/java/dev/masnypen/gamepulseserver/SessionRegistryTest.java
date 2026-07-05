package dev.masnypen.gamepulseserver;

import dev.masnypen.gamepulseserver.services.SessionRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class SessionRegistryTest {

    private SessionRegistry registry;

    @BeforeEach
    public void setUp() {
        registry = new SessionRegistry();
    }

    @Test
    public void shouldRegisterAndRetrieveSession() {
        UUID agentId = UUID.randomUUID();
        WebSocketSession session = mock(WebSocketSession.class);

        registry.register(agentId, session);

        assertTrue(registry.isAuthenticated(session));
        assertEquals(agentId, registry.getAgentId(session));
        assertEquals(session, registry.getSession(agentId));
    }

    @Test
    public void shouldRemoveSession() {
        UUID agentId = UUID.randomUUID();
        WebSocketSession session = mock(WebSocketSession.class);

        registry.register(agentId, session);
        registry.remove(session);

        assertFalse(registry.isAuthenticated(session));
        assertNull(registry.getSession(agentId));
    }
}
