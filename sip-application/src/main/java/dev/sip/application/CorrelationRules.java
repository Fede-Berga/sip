package dev.sip.application;

import dev.sip.domain.*;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public final class CorrelationRules {

    private static final Map<String, Double> ICS_THRESHOLDS = Map.of(
            "TEMP",     90.0,
            "PRESSURE", 150.0,
            "VOLTAGE",  480.0
    );

    public static List<CorrelationRule> defaults() {
        return List.of(
                rule("AUTH_BRUTE_FORCE", SeverityLevel.CRITICAL,
                        event -> event.type() instanceof AuthEvent ae && ae.isFailedSsh()),

                rule("AUTH_FAILURE", SeverityLevel.MEDIUM,
                        event -> event.type() instanceof AuthEvent ae
                                && ae.outcome() == AuthEvent.Outcome.FAILURE),

                rule("AUTH_SUCCESS", SeverityLevel.INFO,
                        event -> event.type() instanceof AuthEvent),

                // --- Network ---
                rule("NET_SSH_INBOUND", SeverityLevel.HIGH,
                        event -> event.type() instanceof NetworkEvent ne
                                && ne.isSshInbound()),

                rule("NET_PORT_SCAN", SeverityLevel.HIGH,
                        event -> event.type() instanceof NetworkEvent ne
                                && ne.isPrivilegedPortScan()),

                rule("NET_OUTBOUND", SeverityLevel.LOW,
                        event -> event.type() instanceof NetworkEvent ne
                                && ne.direction() == NetworkEvent.Direction.OUTBOUND),

                rule("NET_GENERIC", SeverityLevel.INFO,
                        event -> event.type() instanceof NetworkEvent),

                // --- ICS ---
                rule("ICS_THRESHOLD_EXCEEDED", SeverityLevel.HIGH,
                        event -> event.type() instanceof ICSEvent ics
                                && ics.exceedsThreshold(
                                ICS_THRESHOLDS.getOrDefault(ics.tag(), Double.MAX_VALUE))),

                rule("ICS_S7COMM", SeverityLevel.MEDIUM,
                        event -> event.type() instanceof ICSEvent ics
                                && ics.isS7Comm()),

                rule("ICS_GENERIC", SeverityLevel.LOW,
                        event -> event.type() instanceof ICSEvent),

                // --- System ---
                rule("SYS_PRIVESC", SeverityLevel.CRITICAL,
                        event -> event.type() instanceof SystemEvent se
                                && se.isPrivilegeEscalation()),

                rule("SYS_PROCESS_SPAWN", SeverityLevel.MEDIUM,
                        event -> event.type() instanceof SystemEvent se
                                && se.kind() == SystemEvent.Kind.PROCESS_SPAWN),

                rule("SYS_GENERIC", SeverityLevel.LOW,
                        event -> event.type() instanceof SystemEvent)
        );
    }

    private static CorrelationRule rule(String ruleId,
                                        SeverityLevel severity,
                                        Predicate<SecurityEvent> predicate) {
        return new CorrelationRule() {
            @Override
            public boolean matches(SecurityEvent event) {
                return predicate.test(event);
            }

            @Override
            public String ruleId() {
                return ruleId;
            }

            @Override
            public SeverityLevel severity() {
                return severity;
            }
        };
    }
}
