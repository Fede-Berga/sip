package dev.sip.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Finding(
        UUID id,
        SecurityEvent sourceEvent,
        SeverityLevel severity,
        String correlationRuleId,
        String description,
        Instant detectedAt,
        Status status
) {

    public enum Status {OPEN, ACKNOWLEDGED, RESOLVED, FALSE_POSITIVE}

    public Finding {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(sourceEvent, "sourceEvent must not be null");
        Objects.requireNonNull(severity, "severity must not be null");
        Objects.requireNonNull(correlationRuleId, "correlationRuleId must not be null");
        Objects.requireNonNull(detectedAt, "detectedAt must not be null");
        description = description != null ? description : "";
        status = status != null ? status : Status.OPEN;
    }

    public static Finding of(SecurityEvent event,
                             SeverityLevel severity,
                             String ruleId,
                             String description) {
        return new Finding(
                UUID.randomUUID(),
                event,
                severity,
                ruleId,
                description,
                Instant.now(),
                Status.OPEN
        );
    }

    public boolean isCritical() {
        return severity.isAtLeast(SeverityLevel.HIGH);
    }

    public Finding acknowledge() {
        return new Finding(
                id,
                sourceEvent,
                severity,
                correlationRuleId,
                description,
                detectedAt,
                Status.ACKNOWLEDGED
        );
    }
}
