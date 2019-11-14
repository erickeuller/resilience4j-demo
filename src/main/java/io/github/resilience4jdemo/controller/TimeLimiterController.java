package io.github.resilience4jdemo.controller;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4jdemo.connector.TimeLimiterConnector;

@RestController
@RequestMapping("/time-limiter")
public class TimeLimiterController {

    private TimeLimiterConnector connector;

    private TimeLimiter timeLimiter;

    public TimeLimiterController(TimeLimiterConnector connector) {
        this.connector = connector;
        timeLimiter = TimeLimiter.of(TimeLimiterConfig.custom().timeoutDuration(Duration.ofMillis(1L)).build());
    }

    @GetMapping("/failure")
    public ResponseEntity failure() throws Exception {
        Supplier<CompletableFuture<String>> futureSupplier = () -> CompletableFuture.supplyAsync(connector::failure);

        Callable callable = TimeLimiter.decorateFutureSupplier(timeLimiter, futureSupplier);

        callable.call();

        return ResponseEntity.noContent().build();
    }

}
