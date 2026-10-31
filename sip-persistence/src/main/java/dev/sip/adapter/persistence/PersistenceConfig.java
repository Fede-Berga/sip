package dev.sip.adapter.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.sip.domain.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
class PersistenceConfig {

    @Bean
    @Primary
    ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .addMixIn(EventType.class, EventTypeMixin.class);

        mapper.registerSubtypes(
                new NamedType(NetworkEvent.class, "network"),
                new NamedType(AuthEvent.class,    "auth"),
                new NamedType(ICSEvent.class,     "ics"),
                new NamedType(SystemEvent.class,  "system")
        );

        return mapper;
    }
}