package com.mikolajk0wal.unittests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.mikolajk0wal.unittests.Fixtures.productPricedAt;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import({TestDbConfiguration.class, TestExchangeRateConfiguration.class})
class OrderServiceIntegrationTests {
    private static final Instant JUNE_1_2026_9_30 = Instant.parse("2026-06-01T09:30:00Z");

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private EventPublisher eventPublisher;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private Clock clock;

    @BeforeEach
    void setup() {
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        when(clock.instant()).thenReturn(JUNE_1_2026_9_30);
    }

    @Test
    void shouldCreateOrderWithCorrectPriceForGoldCustomer() {
        // Given
        String email = "gold.member@gmail.com";
        customerRepository.save(new Customer(email, 30, CustomerLevel.GOLD));

        // And
        Product tShirt = persisted(productPricedAt(new Money("25", "EUR")));
        List<OrderLineRequest> requests = List.of(new OrderLineRequest(tShirt.id(), 2));

        // When
        UUID orderId = orderService.createOrder(requests, email, "PLN");

        // Then
        Order order = orderRepository.findById(orderId).orElseThrow();
        OrderAssert.assertThat(order).hasTotal("180.00", "PLN");
    }

    private Product persisted(Product product) {
        return productRepository.save(product);
    }
}