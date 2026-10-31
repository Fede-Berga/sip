package dev.sip.application;

import dev.sip.domain.Finding;
import dev.sip.domain.SeverityLevel;
import dev.sip.domain.port.FindingStoragePort;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class QueryFindingsUseCase {
    private final FindingStoragePort storage;

    public QueryFindingsUseCase(FindingStoragePort storage) {
        this.storage = storage;
    }

    public List<Finding> findBySeverity(SeverityLevel minimum, Instant since) {
        return storage.findBySeverityAfter(minimum, since);
    }

    public Optional<Finding> findById(UUID id) {
        return storage.findById(id);
    }

    public List<Finding> findAll() {
        return storage.findAll();
    }
}
