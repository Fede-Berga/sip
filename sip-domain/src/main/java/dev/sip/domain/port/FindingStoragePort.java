package dev.sip.domain.port;

import dev.sip.domain.Finding;
import dev.sip.domain.SeverityLevel;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FindingStoragePort {

    Finding save(Finding finding);

    Optional<Finding> findById(UUID id);

    List<Finding> findBySeverityAfter(SeverityLevel minimum, Instant since);

    List<Finding> findAll();
}
