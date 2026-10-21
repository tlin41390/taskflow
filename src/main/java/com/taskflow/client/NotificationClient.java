package com.taskflow.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
@Component
public class NotificationClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationClient.class);

    private final RestClient restClient;

    public NotificationClient(@Value("${notification-service.service.url}") String baseUrl) {
        HttpClientSettings settings = HttpClientSettings.defaults()
                .withConnectTimeout(Duration.ofSeconds(1))
                .withReadTimeout(Duration.ofSeconds(2));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(ClientHttpRequestFactoryBuilder.detect().build(settings))
                .build();
    }

    public void notifyTaskCreated(Long taskId, String title) {
        try {
            restClient.post()
                .uri("/notifications")
                .body(new NotificationRequest(taskId, "Task created: " + title))
                .retrieve()
                .toBodilessEntity();
        } catch (RestClientException e) {
            log.warn("Failed to notify notification-service for task {}: {}", taskId, e.getMessage());
        }
    }

    private record NotificationRequest(Long taskId, String message) {
    }
}
