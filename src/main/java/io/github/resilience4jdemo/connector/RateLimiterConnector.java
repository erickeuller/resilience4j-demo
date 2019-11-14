package io.github.resilience4jdemo.connector;

import org.springframework.stereotype.Component;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@Component
@RateLimiter(name = "rateLimiter")
public class RateLimiterConnector {

    public String doSomething() {
        return "Success call";
    }

}
