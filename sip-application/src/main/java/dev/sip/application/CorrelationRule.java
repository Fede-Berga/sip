package dev.sip.application;

import dev.sip.domain.SecurityEvent;
import dev.sip.domain.SeverityLevel;

public interface CorrelationRule {
    boolean matches(SecurityEvent event);
    String ruleId();
    SeverityLevel severity();
}
