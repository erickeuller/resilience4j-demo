package io.github.resilience4jdemo.controller;

import java.time.Duration;
import java.util.function.Supplier;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.micrometer.tagged.TaggedRateLimiterMetrics;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4jdemo.connector.RateLimiterConnector;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.vavr.control.Try;

@RestController
@RequestMapping("rate-limiter")
public class RateLimiterController {

    private RateLimiterConnector connector;
    private Supplier<String> supplier;


    public RateLimiterController(RateLimiterConnector connector) {
        this.connector = connector;

        RateLimiterConfig config = RateLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(1))
                .limitRefreshPeriod(Duration.ofSeconds(2))
                .limitForPeriod(1)
                .build();

        RateLimiterRegistry registry = RateLimiterRegistry.of(config);

        RateLimiter rateLimiter = registry.rateLimiter("rateLimiter");

        supplier = RateLimiter.decorateSupplier(rateLimiter, connector::doSomething);

        TaggedRateLimiterMetrics
                .ofRateLimiterRegistry(registry)
                .bindTo(new SimpleMeterRegistry());
    }

    @GetMapping("/failure")
    public ResponseEntity failure() {
        return ResponseEntity.ok(Try.ofSupplier(supplier).isSuccess());
    }
}
