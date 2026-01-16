package com.lifeinventory.identity.infrastructure.event;

import com.lifeinventory.identity.event.IdentityEvent;
import com.lifeinventory.identity.event.IdentityEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Simple logging-based event publisher for debugging.
 */
@Slf4j
@Component
public class LoggingEventPublisher implements IdentityEventPublisher {

    @Override
    public void publish(IdentityEvent event) {
        log.info("Identity event published: {} - {}", event.getClass().getSimpleName(), event);
    }

    @Override
    public void publishAll(List<IdentityEvent> events) {
        events.forEach(this::publish);
    }
}
