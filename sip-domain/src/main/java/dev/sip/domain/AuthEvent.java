package dev.sip.domain;

public record AuthEvent(
        String username,
        Outcome outcome,
        String mechanism
) implements EventType {

    @Override
    public String describe(String sourceIp, SeverityLevel severity) {
        return "[%s] Auth %s for user '%s' via %s from %s"
                .formatted(severity, outcome, username, mechanism, sourceIp);
    }

    public enum Outcome { SUCCESS, FAILURE }

    public boolean isFailedSsh() {
        return outcome == Outcome.FAILURE && mechanism.equals("SSH");
    }
}