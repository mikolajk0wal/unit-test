package com.mikolajk0wal.unittests;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
class SpringApplicationEventPublisher implements EventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    SpringApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(Event event) {
        applicationEventPublisher.publishEvent(event);
    }
}
