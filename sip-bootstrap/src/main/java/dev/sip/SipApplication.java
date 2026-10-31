package dev.sip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SipProperties.class)
public class SipApplication {

    public static void main(String[] args) {
        SpringApplication.run(SipApplication.class, args);
    }
}