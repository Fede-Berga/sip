package dev.sip.application;

import dev.sip.domain.Finding;
import dev.sip.domain.SecurityEvent;
import dev.sip.domain.port.AlertPort;
import dev.sip.domain.port.EventCachePort;
import dev.sip.domain.port.FindingStoragePort;

import java.time.Duration;
import java.util.Optional;

public class IngestEventUseCase {

    private final CorrelationEngine  correlator;
    private final FindingStoragePort storage;
    private final AlertPort          alert;
    private final EventCachePort     cache;

    public IngestEventUseCase(CorrelationEngine correlator,
                              FindingStoragePort storage,
                              AlertPort alert,
                              EventCachePort cache) {
        this.correlator = correlator;
        this.storage    = storage;
        this.alert      = alert;
        this.cache      = cache;
    }

    public Optional<Finding> ingest(SecurityEvent event) {
        if(!cache.markSeenIfNew(event, Duration.ofMinutes(5)))
            return Optional.empty();

        Finding finding = correlator.correlate(event);
        Finding saved = storage.save(finding);

        if(saved.isCritical())
            alert.send(saved);

        return Optional.of(saved);
    }
}