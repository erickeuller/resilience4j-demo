package io.github.resilience4jdemo.controller;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.micrometer.tagged.TaggedCircuitBreakerMetrics;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4jdemo.connector.CircuitBreakerConnector;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@RestController
@RequestMapping("circuit-breaker")
public class CircuitBreakerController {

    private CircuitBreakerConnector connector;

    private CircuitBreaker circuitBreaker;

    public CircuitBreakerController(CircuitBreakerConnector connector, CircuitBreakerRegistry circuitBreakerRegistry) {
        this.connector = connector;
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(1000))
                .permittedNumberOfCallsInHalfOpenState(2)
                .recordExceptions(IOException.class, TimeoutException.class, HttpServerErrorException.class, Throwable.class)
                .recordException(throwable -> throwable instanceof HttpServerErrorException)
                .build();

        circuitBreaker = circuitBreakerRegistry.circuitBreaker("circuitBreakerDecorator");

        TaggedCircuitBreakerMetrics
                .ofCircuitBreakerRegistry(circuitBreakerRegistry)
                .bindTo(new SimpleMeterRegistry());
    }

    @GetMapping("/success")
    public ResponseEntity success() {
        return ResponseEntity.ok(connector.success());
    }

    @GetMapping("/failure")
    public ResponseEntity failure() {
        return ResponseEntity.ok(connector.failure());
    }

    @GetMapping("/failure-decorator")
    public ResponseEntity failureDecorator() {
        return ResponseEntity.ok(CircuitBreaker.decorateSupplier(circuitBreaker, connector::failure).get());
    }

    @GetMapping("/time-limiter-failure")
    public ResponseEntity timeLimiterFailure() throws Exception {

        TimeLimiterConfig config = TimeLimiterConfig.custom().timeoutDuration(Duration.ofMillis(100000000L)).build();

        Supplier<CompletableFuture<String>> futureSupplier = () ->
                CompletableFuture.supplyAsync(connector::timeOutFailure);

        Callable restrictedCall = TimeLimiter
                .decorateFutureSupplier(TimeLimiter.of(config), futureSupplier);

        Callable callable = CircuitBreaker.decorateCallable(circuitBreaker, restrictedCall);
        return ResponseEntity.ok(callable.call());
    }
}
