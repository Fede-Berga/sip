package dev.sip.adapter.persistence;

import dev.sip.domain.SeverityLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

interface FindingJpaRepository extends JpaRepository<FindingEntity, UUID> {

    List<FindingEntity> findBySeverityInAndDetectedAtAfter(
            List<SeverityLevel> severities,
            Instant since
    );
}
