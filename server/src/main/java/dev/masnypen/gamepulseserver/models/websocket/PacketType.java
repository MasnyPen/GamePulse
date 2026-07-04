package dev.masnypen.gamepulseserver.models.websocket;

public enum PacketType {
    GPULSE_AUTH,
    GPULSE_AUTH_ACK,
    GPULSE_AUTH_ERR,
    GPULSE_DENY,

    GPULSE_SYSSTATS,

}
