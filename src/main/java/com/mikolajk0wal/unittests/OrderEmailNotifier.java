package com.mikolajk0wal.unittests;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
class OrderEmailNotifier {
    private final EmailService emailService;

    OrderEmailNotifier(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async
    @EventListener
    public void onOrderCreated(OrderCreated event) {
        emailService.sendEmail(event.customerEmail(), "Your order has been created");
    }
}