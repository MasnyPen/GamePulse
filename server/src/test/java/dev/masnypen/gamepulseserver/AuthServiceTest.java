package dev.masnypen.gamepulseserver;

import dev.masnypen.gamepulseserver.configs.AppConfig;
import dev.masnypen.gamepulseserver.models.websocket.AuthPacketData;
import dev.masnypen.gamepulseserver.repositories.GPAgentRepository;
import dev.masnypen.gamepulseserver.services.SessionRegistry;
import dev.masnypen.gamepulseserver.services.WebSocketAuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    GPAgentRepository repo;

    @Mock
    AppConfig appConfig;

    @Mock
    SessionRegistry sessionRegistry;

    @Mock
    WebSocketSession session;

    @InjectMocks
    WebSocketAuthService authService;

    @Test
    void shouldRejectInvalidToken() throws Exception {
        AuthPacketData data = new AuthPacketData();
        data.setToken("bad");

        boolean result = authService.authenticate(session, data);

        assertFalse(result);
        verify(session).sendMessage(any());
        verify(sessionRegistry, never()).register(any(), any());
    }

}
