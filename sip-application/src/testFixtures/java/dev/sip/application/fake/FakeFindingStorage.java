package dev.sip.application.fake;

import dev.sip.domain.Finding;
import dev.sip.domain.SeverityLevel;
import dev.sip.domain.port.FindingStoragePort;

import java.time.Instant;
import java.util.*;

public class FakeFindingStorage implements FindingStoragePort {

    private final Map<UUID, Finding> store = new LinkedHashMap<>();

    @Override
    public Finding save(Finding finding) {
        store.put(finding.id(), finding);
        return finding;
    }

    @Override
    public Optional<Finding> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Finding> findBySeverityAfter(SeverityLevel minimum, Instant since) {
        return store.values().stream()
                .filter(f -> f.severity().isAtLeast(minimum))
                .filter(f -> f.detectedAt().isAfter(since))
                .toList();
    }

    @Override
    public List<Finding> findAll() {
        return List.copyOf(store.values());
    }

    public int size()  { return store.size(); }
    public void clear() { store.clear(); }
}