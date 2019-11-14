package io.github.resilience4jdemo.connector;


import org.springframework.stereotype.Component;

@Component
public class TimeLimiterConnector {

    public String failure() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        return "Failure";
    }
}
