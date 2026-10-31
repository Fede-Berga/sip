package dev.sip.application;

import dev.sip.domain.*;

import java.util.List;

public class CorrelationEngine {

    private final List<CorrelationRule> rules;

    public CorrelationEngine() {
        this.rules = CorrelationRules.defaults();
    }

    public CorrelationEngine(List<CorrelationRule> rules) {
        this.rules = List.copyOf(rules);
    }

    public Finding correlate(SecurityEvent event) {
        return rules.stream()
                .filter(rule -> rule.matches(event))
                .findFirst()
                .map(rule -> Finding.of(
                        event,
                        rule.severity(),
                        rule.ruleId(),
                        event.type().describe(event.sourceIp(), rule.severity())
                ))
                .orElse(Finding.of(event, SeverityLevel.INFO, "UNKNOWN",
                        "No rule matched for event from " + event.sourceIp()));
    }
}
