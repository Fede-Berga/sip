package dev.sip.domain;

public record SystemEvent(
        String hostname,
        String process,
        int pid,
        Kind kind
) implements EventType {

    @Override
    public String describe(String sourceIp, SeverityLevel severity) {
        return "[%s] System event on %s: %s (pid %d)"
                .formatted(severity, hostname, kind, pid);
    }

    public enum Kind {
        PROCESS_SPAWN,
        PRIVILEGE_ESCALATION,
        FILE_ACCESS,
        NETWORK_SOCKET
    }

    public boolean isPrivilegeEscalation() {
        return kind == Kind.PRIVILEGE_ESCALATION;
    }
}