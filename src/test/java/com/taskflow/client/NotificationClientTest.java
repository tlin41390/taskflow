package com.taskflow.client;

import org.junit.jupiter.api.Test; 

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow; 

public class NotificationClientTest {
    
    @Test 
    void notifyTaskCreatedSwallowsExceptionWhenServiceUnreachable() {
        NotificationClient client = new NotificationClient("http://localhost:1");

        assertDoesNotThrow(() -> client.notifyTaskCreated(1L, "test"));
    }
}
