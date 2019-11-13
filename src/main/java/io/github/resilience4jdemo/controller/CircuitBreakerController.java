package io.github.resilience4jdemo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4jdemo.connector.CircuitBreakerConnector;

@RestController
@RequestMapping("circuit-breaker")
public class CircuitBreakerController {

    private CircuitBreakerConnector connector;

    public CircuitBreakerController(CircuitBreakerConnector connector) {
        this.connector = connector;
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
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .recordExceptions(HttpServerErrorException.class)
                .build();
        return ResponseEntity.ok(CircuitBreaker.of("circuitBreaker", config).decorateSupplier(connector::failure).get());
    }
}
