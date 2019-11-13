package io.github.resilience4jdemo.connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Component
public class CircuitBreakerConnector {

    Logger logger = LoggerFactory.getLogger(CircuitBreakerConnector.class);

    @CircuitBreaker(name = "circuitBreakerConnector")
    public String failure() {
        logger.info("Circuit breaker connector call");
        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public String failureDecorator() {
        return io.github.resilience4j.circuitbreaker.CircuitBreaker.ofDefaults("circuitBreakerDecorator")
                .decorateSupplier(this::failure).get();
    }

    public String success() {
        return "Success call";
    }
}
