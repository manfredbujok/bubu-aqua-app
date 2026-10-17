package tech.taiiku.controlapi.service;

import tech.taiiku.controlapi.dto.SeleniumGridStatus;
import tech.taiiku.controlapi.dto.SeleniumNodeStatus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import tech.taiiku.controlapi.config.ControlApiProperties;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@Service
public class SeleniumStatusService {

    private final ControlApiProperties properties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public SeleniumStatusService(ControlApiProperties properties) {
        this.properties = properties;

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();

        this.objectMapper = new ObjectMapper();
    }

    public SeleniumGridStatus getStatus() throws Exception {

        if (properties.accessClientId() == null
                || properties.accessClientId().isBlank()
                || properties.accessClientSecret() == null
                || properties.accessClientSecret().isBlank()) {

            throw new IllegalStateException(
                    "Cloudflare Access credentials are missing"
            );
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(properties.seleniumStatusUrl()))
                .header("CF-Access-Client-Id", properties.accessClientId())
                .header("CF-Access-Client-Secret", properties.accessClientSecret())
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new IllegalStateException(
                    "Selenium Grid returned HTTP " + response.statusCode()
            );
        }

        JsonNode json = objectMapper.readTree(response.body());
        JsonNode value = json.path("value");
        JsonNode readyNode = value.path("ready");

        if (!readyNode.isBoolean()) {
            throw new IllegalStateException(
                    "Invalid Selenium Grid status response"
            );
        }

        boolean ready = readyNode.asBoolean();

        List<SeleniumNodeStatus> nodes = new ArrayList<>();

        int totalNodes = 0;
        int totalSlots = 0;
        int activeSessions = 0;
        int freeSlots = 0;

        for (JsonNode node : value.path("nodes")) {

            totalNodes++;

            String availability = node.path("availability").asText();
            boolean nodeIsUp = "UP".equalsIgnoreCase(availability);

            // Sessions pro Browser innerhalb dieses Nodes zählen
            Map<String, int[]> browserStats = new LinkedHashMap<>();

            for (JsonNode slot : node.path("slots")) {

                String browser = slot.path("stereotype")
                        .path("browserName")
                        .asText();

                if (browser.isBlank()) {
                    continue;
                }

                int[] stats = browserStats.computeIfAbsent(
                        browser,
                        key -> new int[3]
                );

                // DOWN-Nodes liefern keine nutzbare Kapazität
                if (!nodeIsUp) {
                    continue;
                }

                JsonNode session = slot.path("session");
                boolean occupied = !session.isMissingNode()
                        && !session.isNull();

                stats[0]++; // Slots gesamt
                totalSlots++;

                if (occupied) {
                    stats[1]++; // Aktive Sessions
                    activeSessions++;
                } else {
                    stats[2]++; // Freie Slots
                    freeSlots++;
                }
            }

            for (Map.Entry<String, int[]> entry : browserStats.entrySet()) {

                int[] stats = entry.getValue();

                nodes.add(new SeleniumNodeStatus(
                        entry.getKey(),
                        availability,
                        stats[0],
                        stats[1],
                        stats[2]
                ));
            }
        }

        return new SeleniumGridStatus(
                ready,
                ready ? "Selenium Grid is ready"
                        : "Selenium Grid is not ready",
                totalNodes,
                totalSlots,
                activeSessions,
                freeSlots,
                nodes
        );
    }
}