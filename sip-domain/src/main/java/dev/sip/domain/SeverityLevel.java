package dev.sip.domain;

public enum SeverityLevel {
    INFO,
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    public boolean isAtLeast(SeverityLevel other){
        return this.ordinal() >= other.ordinal();
    }
}