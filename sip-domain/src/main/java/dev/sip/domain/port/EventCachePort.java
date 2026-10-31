package dev.sip.domain.port;

import dev.sip.domain.SecurityEvent;

import java.time.Duration;

public interface EventCachePort {

    boolean markSeenIfNew(SecurityEvent event, Duration ttl);
}
