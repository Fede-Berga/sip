package dev.sip.domain;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record SecurityEvent(
        UUID id,
        EventType type,
        Instant timestamp,
        String sourceIp,
        String rawPayload,
        Map<String, String> metadata
) {

    public SecurityEvent {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(timestamp, "timestamp must not be null");
        Objects.requireNonNull(sourceIp, "sourceIp must not be null");
        rawPayload = rawPayload != null ? rawPayload: "";
        metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }

    public static SecurityEvent of (EventType type, String sourceIp) {
        return new SecurityEvent(
                UUID.randomUUID(),
                type,
                Instant.now(),
                sourceIp,
                "",
                Map.of()
        );
    }

    public static SecurityEvent of ( EventType type,
                                     String sourceIp,
                                     String rawPayload,
                                     Map<String, String> metadata) {
        return new SecurityEvent(
                UUID.randomUUID(),
                type,
                Instant.now(),
                sourceIp,
                rawPayload,
                metadata
        );
    }
}
