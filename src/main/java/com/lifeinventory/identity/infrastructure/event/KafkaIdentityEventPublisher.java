package com.lifeinventory.identity.infrastructure.event;

import com.lifeinventory.identity.event.IdentityEvent;
import com.lifeinventory.identity.event.IdentityEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaIdentityEventPublisher implements IdentityEventPublisher {

    private static final String TOPIC = "identity-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(IdentityEvent event) {
        String key = event.userId() != null ? event.userId().toString() : "system";
        String eventType = event.getClass().getSimpleName();

        kafkaTemplate.send(TOPIC, key, new IdentityEventMessage(eventType, event))
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event {} to Kafka: {}", eventType, ex.getMessage());
                    } else {
                        log.debug("Published event {} to Kafka topic {} partition {}",
                                eventType, TOPIC, result.getRecordMetadata().partition());
                    }
                });
    }

    @Override
    public void publishAll(List<IdentityEvent> events) {
        events.forEach(this::publish);
    }

    public record IdentityEventMessage(String eventType, Object payload) {}
}
