package com.taskflow.client;

import org.junit.jupiter.api.Test; 
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any; 
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class NotificationClientTest {

    @Test 
    void notifyTaskCreatedSendsEventToQueue() {
        JmsTemplate jmsTemplate = mock(JmsTemplate.class);
        NotificationClient client = new NotificationClient(jmsTemplate);

        client.notifyTaskCreated(1L, "ship it");

        verify(jmsTemplate).convertAndSend(eq("task.created.queue"), eq(new TaskCreatedEvent(1L, "ship it")));
    }
    
    @Test 
    void notifyTaskCreatedSwallowsJmsException() {
        JmsTemplate jmsTemplate = mock(JmsTemplate.class); 
        doThrow(new JmsException("broker unreachable") {})
            .when(jmsTemplate).convertAndSend(any(String.class), any(Object.class));
        NotificationClient client = new NotificationClient(jmsTemplate);

        assertDoesNotThrow(() -> client.notifyTaskCreated(1L, "ship it"));
    }
}
