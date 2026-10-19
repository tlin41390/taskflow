package com.taskflow.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component; 
import org.springframework.web.client.RestClient; 

@Component
public class NotificationClient {
    private final RestClient restClient;

    public NotificationClient(@Value("${notification-service.service.url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public void notifyTaskCreated(Long taskId, String title) {
        restClient.post()
            .uri("/notifications")
            .body(new NotificationRequest(taskId, "Task created: " + title))
            .retrieve()
            .toBodilessEntity();
    }

    private record NotificationRequest(Long taskId, String message) {
    }
}
