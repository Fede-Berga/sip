package dev.sip.application;

import dev.sip.application.fake.FakeAlertPort;
import dev.sip.application.fake.FakeEventCache;
import dev.sip.application.fake.FakeFindingStorage;
import dev.sip.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class IngestEventUseCaseTest {

    private FakeFindingStorage storage;
    private FakeAlertPort      alert;
    private FakeEventCache     cache;
    private IngestEventUseCase useCase;

    @BeforeEach
    void setUp() {
        storage = new FakeFindingStorage();
        alert   = new FakeAlertPort();
        cache   = new FakeEventCache();
        useCase = new IngestEventUseCase(
                new CorrelationEngine(), storage, alert, cache
        );
    }

    @Test
    void shouldSaveFindingWhenEventIngested() {
        var event = SecurityEvent.of(
                new AuthEvent("admin", AuthEvent.Outcome.FAILURE, "SSH"),
                "10.0.0.1"
        );

        Optional<Finding> result = useCase.ingest(event);

        assertTrue(result.isPresent());
        assertEquals(1, storage.size());
    }

    @Test
    void shouldSendAlertWhenFindingIsCritical() {
        var event = SecurityEvent.of(
                new SystemEvent("host", "evil", 999,
                        SystemEvent.Kind.PRIVILEGE_ESCALATION),
                "10.0.0.2"
        );

        useCase.ingest(event);

        assertTrue(alert.wasAlerted());
        assertEquals(SeverityLevel.CRITICAL, alert.sentAlerts().get(0).severity());
    }

    @Test
    void shouldNotSendAlertForLowSeverity() {
        var event = SecurityEvent.of(
                new NetworkEvent("TCP", 1234, 80,
                        NetworkEvent.Direction.OUTBOUND),
                "10.0.0.3"
        );

        useCase.ingest(event);

        assertFalse(alert.wasAlerted());
        assertEquals(1, storage.size());
    }

    @Test
    void shouldDiscardDuplicateEvents() {
        var event = SecurityEvent.of(
                new AuthEvent("user", AuthEvent.Outcome.FAILURE, "LDAP"),
                "10.0.0.4"
        );

        var first    = useCase.ingest(event);
        var duplicate = useCase.ingest(event);

        assertTrue(first.isPresent());
        assertFalse(duplicate.isPresent());
        assertEquals(1, storage.size());
    }

    @Test
    void shouldReturnEmptyForDuplicate() {
        var event = SecurityEvent.of(
                new ICSEvent("PLC-01", "TEMP", 20.0, ICSEvent.Protocol.MODBUS),
                "172.16.0.1"
        );

        var first  = useCase.ingest(event);
        var second = useCase.ingest(event);

        assertTrue(first.isPresent());
        assertFalse(second.isPresent());
    }

    @Test
    void shouldQueryFindingsBySeverity() {
        var critical = SecurityEvent.of(
                new SystemEvent("h1", "evil", 1,
                        SystemEvent.Kind.PRIVILEGE_ESCALATION), "1.1.1.1");
        var low = SecurityEvent.of(
                new NetworkEvent("TCP", 1234, 80,
                        NetworkEvent.Direction.OUTBOUND), "2.2.2.2");

        useCase.ingest(critical);
        useCase.ingest(low);

        var queryUseCase = new QueryFindingsUseCase(storage);
        var highFindings = queryUseCase.findBySeverity(
                SeverityLevel.HIGH, Instant.now().minusSeconds(60));

        assertEquals(1, highFindings.size());
        assertEquals(SeverityLevel.CRITICAL, highFindings.get(0).severity());
    }
}