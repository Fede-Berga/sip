package dev.sip.adapter.persistence;

import dev.sip.domain.Finding;
import dev.sip.domain.SeverityLevel;
import dev.sip.domain.port.FindingStoragePort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Transactional
public class JpaFindingAdapter implements FindingStoragePort {

    private final FindingJpaRepository repo;
    private final FindingMapper mapper;

    JpaFindingAdapter(FindingJpaRepository repo, FindingMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public Finding save(Finding finding) {
        return mapper.toDomain(repo.save(mapper.toEntity(finding)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Finding> findById(UUID id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Finding> findBySeverityAfter(SeverityLevel minimum, Instant since) {
        List<SeverityLevel> levels = Arrays.stream(SeverityLevel.values())
                .filter(s -> s.isAtLeast(minimum))
                .toList();
        return repo.findBySeverityInAndDetectedAtAfter(levels, since)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Finding> findAll() {
        return repo.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
