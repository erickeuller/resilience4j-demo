package io.github.resilience4jdemo.connector;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

import io.github.resilience4j.retry.annotation.Retry;

@Component
@Retry(name = "retryConnector")
public class RetryConnector {

    public String failure() {
        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
