package com.taskflow.config;

import com.taskflow.client.TaskCreatedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.JacksonJsonMessageConverter;
import org.springframework.boot.artemis.autoconfigure.ArtemisConfigurationCustomizer;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.Map;

@Configuration
public class JmsConfig {

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setTypeIdMappings(Map.of("task-created", TaskCreatedEvent.class));
        return converter;
    }

    @Bean
    public ArtemisConfigurationCustomizer artemisConfigurationCustomizer() {
        return configuration -> {
            try {
                configuration.addAcceptorConfiguration("netty", "tcp://0.0.0.0:61616");
            } catch (Exception ex) {
                throw new IllegalStateException("Could not add Artemis acceptor", ex);
            }
        };
    }
}
