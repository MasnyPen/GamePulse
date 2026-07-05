package dev.masnypen.gamepulseserver;

import dev.masnypen.gamepulseserver.components.PacketRouter;
import dev.masnypen.gamepulseserver.services.SessionRegistry;
import dev.masnypen.gamepulseserver.services.SysStatsService;
import dev.masnypen.gamepulseserver.services.WebSocketAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.WebSocketSession;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PacketRouterTest {

    @Mock
    WebSocketAuthService authService;

    @Mock
    SysStatsService sysStatsService;

    @Mock
    SessionRegistry sessionRegistry;

    PacketRouter router;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        router = new PacketRouter(authService, sysStatsService, sessionRegistry);
    }

    @Test
    void shouldRouteAuthPacket() {
        WebSocketSession session = mock(WebSocketSession.class);

        router.route(session, "GPULSE_AUTH;{}");

        verify(authService).authenticate(eq(session), any());
    }
}