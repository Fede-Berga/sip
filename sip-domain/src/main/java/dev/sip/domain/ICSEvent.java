package dev.sip.domain;

public record ICSEvent(
        String deviceId,
        String tag,
        double value,
        Protocol protocol
) implements EventType {

    @Override
    public String describe(String sourceIp, SeverityLevel severity) {
        return "[%s] ICS event on device %s tag %s value %.2f (%s)"
                .formatted(severity, deviceId, tag, value, protocol);
    }

    public enum Protocol { MODBUS, DNPV3, S7COMM, OPC_UA }

    public boolean exceedsThreshold(double threshold) {
        return value > threshold;
    }

    public boolean isS7Comm() {
        return protocol == Protocol.S7COMM;
    }
}