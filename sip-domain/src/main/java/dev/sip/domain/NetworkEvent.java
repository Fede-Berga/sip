package dev.sip.domain;

public record NetworkEvent(
        String protocol,
        int srcPort,
        int dstPort,
        Direction direction
) implements EventType {

    @Override
    public String describe(String sourceIp, SeverityLevel severity) {
        return "[%s] Network event from %s: %s port %d→%d"
                .formatted(severity, sourceIp, protocol, srcPort, dstPort);
    }

    public enum Direction {INBOUND, OUTBOUND, LATERAL}

    public boolean isSshInbound() {
        return dstPort == 22 && direction == Direction.INBOUND;
    }

    public boolean isPrivilegedPortScan() {
        return dstPort < 1024 && direction == Direction.INBOUND;
    }
}