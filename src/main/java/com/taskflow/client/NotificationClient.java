package com.taskflow.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.JmsException; 
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationClient.class);
    private static final String QUEUE = "task.created.queue";

    private final JmsTemplate jmsTemplate;

    public NotificationClient(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void notifyTaskCreated(Long taskId, String title) {
        try {
            jmsTemplate.convertAndSend(QUEUE, new TaskCreatedEvent(taskId, title));
        } catch (JmsException e) {
            log.warn("Failed to publish task-created event for task {}: {}", taskId, e.getMessage());
        }
    }
}
