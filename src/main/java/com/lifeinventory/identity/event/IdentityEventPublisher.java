package com.lifeinventory.identity.event;

import java.util.List;

/**
 * Interface for publishing identity domain events.
 * Output port - implementation provided by infrastructure layer.
 */
public interface IdentityEventPublisher {

    /**
     * Publish a single event.
     *
     * @param event the event to publish
     */
    void publish(IdentityEvent event);

    /**
     * Publish multiple events.
     *
     * @param events the events to publish
     */
    void publishAll(List<IdentityEvent> events);
}
