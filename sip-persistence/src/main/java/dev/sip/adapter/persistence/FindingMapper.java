package dev.sip.adapter.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sip.domain.*;
import org.springframework.stereotype.Component;

@Component
class FindingMapper {

    private final ObjectMapper objectMapper;

    FindingMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    FindingEntity toEntity(Finding finding) {
        return FindingEntity.builder()
                .id(finding.id())
                .severity(finding.severity())
                .correlationRuleId(finding.correlationRuleId())
                .description(finding.description())
                .sourceIp(finding.sourceEvent().sourceIp())
                .eventPayload(serialize(finding.sourceEvent().type()))
                .detectedAt(finding.detectedAt())
                .status(finding.status())
                .build();
    }

    Finding toDomain(FindingEntity entity) {
        return new Finding(
                entity.getId(),
                SecurityEvent.of(
                        deserialize(entity.getEventPayload()),
                        entity.getSourceIp()
                ),
                entity.getSeverity(),
                entity.getCorrelationRuleId(),
                entity.getDescription(),
                entity.getDetectedAt(),
                entity.getStatus()
        );
    }

    private String serialize(EventType type) {
        try {
            return objectMapper.writeValueAsString(type);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialize EventType", e);
        }
    }

    private EventType deserialize(String json) {
        try {
            return objectMapper.readValue(json, EventType.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot deserialize EventType", e);
        }
    }
}