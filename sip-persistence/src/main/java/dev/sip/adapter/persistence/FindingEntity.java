package dev.sip.adapter.persistence;

import dev.sip.domain.Finding;
import dev.sip.domain.SeverityLevel;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "findings")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class FindingEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeverityLevel severity;

    @Column(name = "correlation_rule_id", nullable = false)
    private String correlationRuleId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "source_ip", nullable = false)
    private String sourceIp;

    @Column(name = "event_payload", columnDefinition = "jsonb")
    private String eventPayload;

    @Column(name = "detected_at", nullable = false)
    private Instant detectedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Finding.Status status;
}