package com.lifeinventory.identity.infrastructure.event;

import com.lifeinventory.identity.event.IdentityEvent;
import com.lifeinventory.identity.event.IdentityEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Composite event publisher that delegates to multiple publishers.
 * Publishes to both Kafka (for inter-service communication) and logging (for debugging).
 */
@Slf4j
@Component
@Primary
public class CompositeEventPublisher implements IdentityEventPublisher {

    private final LoggingEventPublisher loggingPublisher;
    private final KafkaIdentityEventPublisher kafkaPublisher;

    public CompositeEventPublisher(
            LoggingEventPublisher loggingPublisher,
            KafkaIdentityEventPublisher kafkaPublisher
    ) {
        this.loggingPublisher = loggingPublisher;
        this.kafkaPublisher = kafkaPublisher;
    }

    @Override
    public void publish(IdentityEvent event) {
        loggingPublisher.publish(event);
        try {
            kafkaPublisher.publish(event);
        } catch (Exception e) {
            log.warn("Failed to publish event to Kafka, continuing: {}", e.getMessage());
        }
    }

    @Override
    public void publishAll(List<IdentityEvent> events) {
        events.forEach(this::publish);
    }
}
