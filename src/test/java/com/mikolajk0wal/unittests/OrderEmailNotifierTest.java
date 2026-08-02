package com.mikolajk0wal.unittests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OrderEmailNotifierTest {
    private EmailService emailService;
    private OrderEmailNotifier notifier;

    @BeforeEach
    void setUp() {
        this.emailService = mock(EmailService.class);
        this.notifier = new OrderEmailNotifier(emailService);
    }

    @Test
    void shouldSendConfirmationEmailOnOrderCreated() {
        // Given
        String email = "test@gmail.com";
        OrderCreated event = new OrderCreated(UUID.randomUUID(), email);

        // When
        notifier.onOrderCreated(event);

        // Then
        verify(emailService).sendEmail(email, "Your order has been created");
    }
}