package io.github.resilience4jdemo.connector;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import org.springframework.stereotype.Component;

@Component
public class BulkheadConnector {

    @Bulkhead(name = "bulkheadConnector")
    public String success() {
        return "Hello from bulkhead";
    }
}
