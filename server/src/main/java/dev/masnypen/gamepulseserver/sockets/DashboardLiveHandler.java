package dev.masnypen.gamepulseserver.sockets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.masnypen.gamepulseserver.models.dashboard.DashboardSubscription;
import dev.masnypen.gamepulseserver.models.dashboard.MetricSection;
import dev.masnypen.gamepulseserver.services.dashboard.DashboardLiveUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DashboardLiveHandler extends TextWebSocketHandler {

    private final DashboardLiveUpdateService liveUpdateService;
    private final Gson gson = new Gson();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        liveUpdateService.register(session);
        sendJson(session, okMessage("connected"));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        liveUpdateService.unregister(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        JsonObject root;
        try {
            root = JsonParser.parseString(message.getPayload()).getAsJsonObject();
        } catch (Exception e) {
            sendJson(session, errorMessage("invalid_json"));
            return;
        }

        String type = readString(root, "type");
        if (type == null) {
            sendJson(session, errorMessage("missing_type"));
            return;
        }

        switch (type) {
            case "set_filter" -> handleSetFilter(session, root);
            case "ping" -> sendJson(session, okMessage("pong"));
            default -> sendJson(session, errorMessage("unsupported_type"));
        }
    }

    private void handleSetFilter(WebSocketSession session, JsonObject root) throws IOException {
        try {
            Set<UUID> agentIds = parseAgentIds(root.getAsJsonArray("agent_ids"));
            Set<MetricSection> metricSections = MetricSection.fromRawNames(parseStrings(root.getAsJsonArray("metrics")));

            liveUpdateService.setFilter(session, agentIds, metricSections);

            DashboardSubscription subscription = liveUpdateService.getSubscription(session);
            JsonObject response = okMessage("filter_updated");
            response.add("agent_ids", gson.toJsonTree(
                    subscription.getAgentIds().stream().map(UUID::toString).collect(Collectors.toSet())
            ));
            response.add("metrics", gson.toJsonTree(
                    subscription.getMetricSections().stream().map(Enum::name).toList()
            ));
            sendJson(session, response);
        } catch (IllegalArgumentException e) {
            sendJson(session, errorMessage("invalid_filter_value"));
        }
    }

    private Set<UUID> parseAgentIds(JsonArray values) {
        if (values == null) {
            return Set.of();
        }
        Set<UUID> ids = new HashSet<>();
        for (var value : values) {
            ids.add(UUID.fromString(value.getAsString()));
        }
        return ids;
    }

    private Set<String> parseStrings(JsonArray values) {
        if (values == null) {
            return Set.of();
        }
        Set<String> parsed = new HashSet<>();
        for (var value : values) {
            parsed.add(value.getAsString());
        }
        return parsed;
    }

    private String readString(JsonObject root, String key) {
        if (!root.has(key) || root.get(key).isJsonNull()) {
            return null;
        }
        return root.get(key).getAsString();
    }

    private JsonObject okMessage(String event) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "ok");
        root.addProperty("event", event);
        return root;
    }

    private JsonObject errorMessage(String error) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "error");
        root.addProperty("error", error);
        return root;
    }

    private void sendJson(WebSocketSession session, JsonObject payload) throws IOException {
        session.sendMessage(new TextMessage(gson.toJson(payload)));
    }
}
