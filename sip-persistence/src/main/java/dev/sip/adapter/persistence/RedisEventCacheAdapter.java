package dev.sip.adapter.rest;

import dev.sip.domain.SecurityEvent;
import dev.sip.domain.port.EventCachePort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisEventCacheAdapter implements EventCachePort {

    private static final String KEY_PREFIX = "sip:event:";
    private final StringRedisTemplate redis;

    RedisEventCacheAdapter(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public boolean markSeenIfNew(SecurityEvent event, Duration ttl) {
        Boolean isNew = redis.opsForValue()
                .setIfAbsent(cacheKey(event), "1", ttl);
        return Boolean.TRUE.equals(isNew);
    }

    private String cacheKey(SecurityEvent event) {
        return KEY_PREFIX
                + event.sourceIp()
                + ":"
                + event.type().getClass().getSimpleName();
    }
}
