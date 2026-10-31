package dev.sip.domain;

public sealed interface EventType
        permits NetworkEvent, AuthEvent, ICSEvent, SystemEvent {

    String describe(String sourceIp, SeverityLevel severity);
}