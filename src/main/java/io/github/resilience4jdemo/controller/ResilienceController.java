package io.github.resilience4jdemo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4jdemo.connector.ResilienceConnector;

@RestController
@RequestMapping("/resilience")
public class ResilienceController {

    private ResilienceConnector connector;

    public ResilienceController(ResilienceConnector connector) {
        this.connector = connector;
    }

    @GetMapping("/failure")
    public ResponseEntity failure() {
        return ResponseEntity.ok(connector.failure());
    }
}
