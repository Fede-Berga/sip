package dev.sip.application;

import dev.sip.domain.Finding;
import dev.sip.domain.SecurityEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.StructuredTaskScope;

public class CorrelateEventsBatchUseCase {

    private final IngestEventUseCase ingestUseCase;

    public CorrelateEventsBatchUseCase(IngestEventUseCase ingestUseCase) {
        this.ingestUseCase = ingestUseCase;
    }

    public List<Finding> processBatch(List<SecurityEvent> events)
            throws ExecutionException, InterruptedException {

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Optional<Finding>>> futures = events.stream()
                    .map(event -> executor.submit(() -> ingestUseCase.ingest(event)))
                    .toList();

            List<Finding> results = new ArrayList<>();
            for(var future : futures) {
                future.get().ifPresent(results::add);
            }

            return results;
        }
    }
}
