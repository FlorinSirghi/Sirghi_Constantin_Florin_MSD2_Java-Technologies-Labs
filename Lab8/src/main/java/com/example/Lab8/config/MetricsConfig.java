package com.example.Lab8.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter stableMatchCounter(MeterRegistry registry) {
        return Counter.builder("stable_match.invocations")
                .description("Number of times the StableMatch algorithm is invoked")
                .register(registry);
    }

    @Bean
    public Timer stableMatchTimer(MeterRegistry registry) {
        return Timer.builder("stable_match.duration")
                .description("Response time of the StableMatch algorithm")
                .register(registry);
    }
}





