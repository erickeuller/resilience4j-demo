package io.github.resilience4jdemo.controller;

import java.time.Duration;
import java.util.function.Supplier;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

import io.github.resilience4j.micrometer.tagged.TaggedRetryMetrics;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4jdemo.connector.RetryConnector;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@RestController
@RequestMapping("/retry")
public class RetryController {

    private RetryConnector connector;

    public RetryController(RetryConnector connector) {
        this.connector = connector;
    }

    @GetMapping("/failure")
    public ResponseEntity failure() throws Throwable {
        return ResponseEntity.ok(connector.failure());
    }

    @GetMapping("/failure-decorator")
    public ResponseEntity failureWithDecorator() throws Throwable {
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(1000))
                .retryExceptions(HttpServerErrorException.class)
                .build();
        RetryRegistry registry = RetryRegistry.ofDefaults();

        Retry retry = registry.retry("retryConnector", retryConfig);
        TaggedRetryMetrics
                .ofRetryRegistry(registry)
                .bindTo(new SimpleMeterRegistry());

        Supplier<String> retryableFunction = Retry.decorateSupplier(retry, connector::failure);
        return ResponseEntity.ok(retryableFunction.get());
    }
}
