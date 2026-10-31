package dev.sip.application;

import dev.sip.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

public class CorrelationEngineTest {
    private CorrelationEngine engine;

    @BeforeEach
    void setUp() {
        engine = new CorrelationEngine();
    }

    @Test
    void sshInboundShouldBeHigh() {
        var event = SecurityEvent.of(
                new NetworkEvent("TCP", 12345, 22, NetworkEvent.Direction.INBOUND),
                "10.0.0.1"
        );
        var finding = engine.correlate(event);
        assertEquals(SeverityLevel.HIGH, finding.severity());
        assertEquals("NET_SSH_INBOUND", finding.correlationRuleId());
    }

    @Test
    void privilegedPortScanShouldBeHigh() {
        var event = SecurityEvent.of(
                new NetworkEvent("TCP", 54321, 443, NetworkEvent.Direction.INBOUND),
                "10.0.0.2"
        );
        var finding = engine.correlate(event);
        assertEquals(SeverityLevel.HIGH, finding.severity());
        assertEquals("NET_PORT_SCAN", finding.correlationRuleId());
    }

    @Test
    void outboundNetworkShouldBeLow() {
        var event = SecurityEvent.of(
                new NetworkEvent("TCP", 12345, 80, NetworkEvent.Direction.OUTBOUND),
                "10.0.0.3"
        );
        var finding = engine.correlate(event);
        assertEquals(SeverityLevel.LOW, finding.severity());
    }

    // --- AuthEvent ---

    @Test
    void failedSshShouldBeCritical() {
        var event = SecurityEvent.of(
                new AuthEvent("admin", AuthEvent.Outcome.FAILURE, "SSH"),
                "192.168.1.1"
        );
        var finding = engine.correlate(event);
        assertEquals(SeverityLevel.CRITICAL, finding.severity());
        assertEquals("AUTH_BRUTE_FORCE", finding.correlationRuleId());
    }

    @Test
    void failedNonSshShouldBeMedium() {
        var event = SecurityEvent.of(
                new AuthEvent("user", AuthEvent.Outcome.FAILURE, "LDAP"),
                "192.168.1.2"
        );
        var finding = engine.correlate(event);
        assertEquals(SeverityLevel.MEDIUM, finding.severity());
        assertEquals("AUTH_FAILURE", finding.correlationRuleId());
    }

    @Test
    void successfulAuthShouldBeInfo() {
        var event = SecurityEvent.of(
                new AuthEvent("user", AuthEvent.Outcome.SUCCESS, "LDAP"),
                "192.168.1.3"
        );
        var finding = engine.correlate(event);
        assertEquals(SeverityLevel.INFO, finding.severity());
    }

    // --- ICSEvent ---

    @Test
    void icsOverTemperatureThresholdShouldBeHigh() {
        var event = SecurityEvent.of(
                new ICSEvent("PLC-01", "TEMP", 95.0, ICSEvent.Protocol.MODBUS),
                "172.16.0.1"
        );
        var finding = engine.correlate(event);
        assertEquals(SeverityLevel.HIGH, finding.severity());
        assertEquals("ICS_THRESHOLD_EXCEEDED", finding.correlationRuleId());
    }

    @Test
    void icsS7CommShouldBeMedium() {
        var event = SecurityEvent.of(
                new ICSEvent("PLC-02", "TEMP", 20.0, ICSEvent.Protocol.S7COMM),
                "172.16.0.2"
        );
        var finding = engine.correlate(event);
        assertEquals(SeverityLevel.MEDIUM, finding.severity());
        assertEquals("ICS_S7COMM", finding.correlationRuleId());
    }

    // --- SystemEvent ---

    @Test
    void privilegeEscalationShouldBeCritical() {
        var event = SecurityEvent.of(
                new SystemEvent("server-01", "sudo", 1234,
                        SystemEvent.Kind.PRIVILEGE_ESCALATION),
                "10.10.0.1"
        );
        var finding = engine.correlate(event);
        assertEquals(SeverityLevel.CRITICAL, finding.severity());
        assertEquals("SYS_PRIVESC", finding.correlationRuleId());
    }

    @Test
    void processSpawnShouldBeMedium() {
        var event = SecurityEvent.of(
                new SystemEvent("server-02", "bash", 5678,
                        SystemEvent.Kind.PROCESS_SPAWN),
                "10.10.0.2"
        );
        var finding = engine.correlate(event);
        assertEquals(SeverityLevel.MEDIUM, finding.severity());
        assertEquals("SYS_PROCESS_SPAWN", finding.correlationRuleId());
    }

    // --- Finding properties ---

    @Test
    void findingShouldHaveNonNullId() {
        var event = SecurityEvent.of(
                new AuthEvent("admin", AuthEvent.Outcome.FAILURE, "SSH"),
                "1.2.3.4"
        );
        var finding = engine.correlate(event);
        assertNotNull(finding.id());
        assertNotNull(finding.detectedAt());
        assertEquals(Finding.Status.OPEN, finding.status());
    }

    @Test
    void criticalFindingShouldReportIsCritical() {
        var event = SecurityEvent.of(
                new SystemEvent("host", "evil", 999,
                        SystemEvent.Kind.PRIVILEGE_ESCALATION),
                "1.2.3.4"
        );
        var finding = engine.correlate(event);
        assertTrue(finding.isCritical());
    }

    @ParameterizedTest
    @EnumSource(ICSEvent.Protocol.class)
    void allIcsProtocolsShouldProduceFinding(ICSEvent.Protocol protocol) {
        var event = SecurityEvent.of(
                new ICSEvent("PLC-99", "TEMP", 20.0, protocol),
                "172.16.0.99"
        );
        var finding = engine.correlate(event);
        assertNotNull(finding);
        assertNotNull(finding.severity());
    }
}
