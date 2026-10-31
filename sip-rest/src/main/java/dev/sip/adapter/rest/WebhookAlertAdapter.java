package dev.sip.adapter.rest;

import dev.sip.domain.Finding;
import dev.sip.domain.port.AlertPort;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
class WebhookAlertAdapter implements AlertPort {

    private static final Logger log = LoggerFactory.getLogger(WebhookAlertAdapter.class);

    private final RestClient restClient;
    private final String     webhookUrl;

    WebhookAlertAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${sip.alerts.webhook-url:}") String webhookUrl) {
        this.restClient = restClientBuilder.build();
        this.webhookUrl = webhookUrl;
    }

    @Override
    public void send(Finding finding) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.warn("[ALERT] Webhook URL not configured — finding {} suppressed",
                    finding.id());
            return;
        }
        try {
            restClient.post()
                    .uri(webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new AlertPayload(
                            finding.id().toString(),
                            finding.severity().name(),
                            finding.correlationRuleId(),
                            finding.description(),
                            finding.sourceEvent().sourceIp(),
                            finding.detectedAt().toString()
                    ))
                    .retrieve()
                    .toBodilessEntity();

            log.info("[ALERT] Webhook sent for finding {} severity={}",
                    finding.id(), finding.severity());

        } catch (RestClientException e) {
            log.error("[ALERT] Webhook delivery failed for finding {}: {}",
                    finding.id(), e.getMessage());
        }
    }

    record AlertPayload(
            String findingId,
            String severity,
            String ruleId,
            String description,
            String sourceIp,
            String detectedAt
    ) {}
}