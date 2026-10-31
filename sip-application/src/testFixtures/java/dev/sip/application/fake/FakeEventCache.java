package dev.sip.application.fake;

import dev.sip.domain.SecurityEvent;
import dev.sip.domain.port.EventCachePort;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class FakeEventCache implements EventCachePort {

    private final Set<String> seen = new HashSet<>();

    @Override
    public boolean markSeenIfNew(SecurityEvent event, Duration ttl) {
        return seen.add(cacheKey(event));   // true se aggiunto (nuovo), false se già presente
    }

    private String cacheKey(SecurityEvent e) {
        return e.sourceIp() + ":" + e.type().getClass().getSimpleName();
    }

    public void clear() { seen.clear(); }
}