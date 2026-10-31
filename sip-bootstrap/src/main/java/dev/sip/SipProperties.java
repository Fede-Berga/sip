package dev.sip;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("sip")
public record SipProperties(
        Alerts alerts
) {
    public record Alerts(
            String webhookUrl
    ) {}
}
