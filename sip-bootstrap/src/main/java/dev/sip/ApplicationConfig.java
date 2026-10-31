package dev.sip;

import dev.sip.application.CorrelateEventsBatchUseCase;
import dev.sip.application.CorrelationEngine;
import dev.sip.application.IngestEventUseCase;
import dev.sip.application.QueryFindingsUseCase;
import dev.sip.domain.port.AlertPort;
import dev.sip.domain.port.EventCachePort;
import dev.sip.domain.port.FindingStoragePort;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import javax.sql.DataSource;

@Configuration
public class ApplicationConfig {

    @Bean
    SpringLiquibase liquibase(DataSource dataSource) {
        var liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.xml");
        return liquibase;
    }

    @Bean
    RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    CorrelationEngine correlationEngine() {
        return new CorrelationEngine();
    }

    @Bean
    IngestEventUseCase ingestEventUseCase(
            CorrelationEngine engine,
            FindingStoragePort storage,
            AlertPort alert,
            EventCachePort cache) {
        return new IngestEventUseCase(engine, storage, alert, cache);
    }

    @Bean
    QueryFindingsUseCase queryFindingsUseCase(FindingStoragePort storage) {
        return new QueryFindingsUseCase(storage);
    }

    @Bean
    CorrelateEventsBatchUseCase correlateEventsBatchUseCase(
            IngestEventUseCase ingestUseCase) {
        return new CorrelateEventsBatchUseCase(ingestUseCase);
    }
}
