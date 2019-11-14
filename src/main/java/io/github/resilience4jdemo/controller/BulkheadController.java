package io.github.resilience4jdemo.controller;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.micrometer.tagged.TaggedBulkheadMetrics;
import io.github.resilience4jdemo.connector.BulkheadConnector;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/bulkhead")
public class BulkheadController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private BulkheadConnector connector;

    private Bulkhead bulkhead;

    public BulkheadController(BulkheadConnector connector, BulkheadRegistry registry) {
        this.connector = connector;
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(1)
                .maxWaitDuration(Duration.ofMillis(1))
                .build();

        bulkhead = registry.bulkhead("bulkheadDecorator", config);
        bulkhead.getEventPublisher()
                .onCallRejected(event -> logger.info(event.getBulkheadName()));
        TaggedBulkheadMetrics
                .ofBulkheadRegistry(registry)
                .bindTo(new SimpleMeterRegistry());
    }

    @GetMapping("/success")
    public ResponseEntity success() {
        return ResponseEntity.ok(connector.success());
    }

    @GetMapping("/success-decorator")
    public ResponseEntity failure() {
        return ResponseEntity.ok(Bulkhead.decorateSupplier(bulkhead, () -> "Hello").get());
    }
}
