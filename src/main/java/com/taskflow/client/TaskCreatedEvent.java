package com.taskflow.client;

public record TaskCreatedEvent(Long taskId, String title) {
}
