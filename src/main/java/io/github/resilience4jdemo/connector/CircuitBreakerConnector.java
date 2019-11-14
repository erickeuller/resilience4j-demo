package io.github.resilience4jdemo.connector;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

@Component
@CircuitBreaker(name = "circuitBreakerConnector")
public class CircuitBreakerConnector {

    public String failure() {
        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public String timeOutFailure() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {}
        return "failure";
    }

    public String success() {
        return "Success call";
    }
}
