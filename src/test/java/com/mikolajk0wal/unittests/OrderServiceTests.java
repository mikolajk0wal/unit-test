package com.mikolajk0wal.unittests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.mikolajk0wal.unittests.Fixtures.productPricedAt;
import static com.mikolajk0wal.unittests.OrderAssert.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OrderServiceTests {
    private static final LocalDateTime JUNE_1_2026_9_30 = LocalDateTime.of(2026, 6, 1, 9, 30);
    private ProductRepository productRepository;
    private OrderRepository orderRepository;
    private EmailService emailService;
    private OrderService orderService;

    @BeforeEach
    void setup() {
        productRepository = new InMemoryProductRepository();
        orderRepository = new InMemoryOrderRepository();
        PriceCalculator priceCalculator = new PriceCalculator();

        ExchangeRateProvider exchangeRateProvider = () -> new ExchangeRates("PLN",
                Map.of("EUR", new BigDecimal("0.25"), "GBP", new BigDecimal("0.20"), "CHF", new BigDecimal("0.22")));

        emailService = mock(EmailService.class);

        Clock clock = Clock.fixed(JUNE_1_2026_9_30.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        orderService = new OrderService(productRepository, orderRepository, priceCalculator, exchangeRateProvider,
                emailService, clock);

    }

    @Test
    void shouldCreateOrderWithCorrectPrice() {
        // Given
        Product tShirt = persisted(productPricedAt(new Money("25", "EUR")));

        // And
        List<OrderLineRequest> requests = List.of(new OrderLineRequest(tShirt.id(), 2));

        // When
        UUID orderId = orderService.createOrder(requests, "test@gmail.com", "PLN");

        // Then
        Order order = orderRepository.findById(orderId).orElseThrow();
        assertThat(order).hasTotal("200", "PLN");
    }

    @Test
    void shouldCreateOrderWithMultipleDifferentProducts() {
        // Given
        Product socks = persisted(productPricedAt(new Money("10.00", "EUR")));
        Product tShirt = persisted(productPricedAt(new Money("90.00", "PLN")));

        // And
        List<OrderLineRequest> requests = List.of(new OrderLineRequest(socks.id(), 1),
                new OrderLineRequest(tShirt.id(), 2));

        // When
        UUID orderId = orderService.createOrder(requests, "test@gmail.com", "PLN");

        // Then
        Order order = orderRepository.findById(orderId).orElseThrow();
        assertThat(order)
            .hasLine(socks, 1)
            .hasLine(tShirt, 2);
    }

    @Test
    void shouldMergeQuantitiesWhenSameProductAppearsInMultipleLines() {
        // Given
        Product socks = persisted(productPricedAt(new Money("10.00", "EUR")));

        // And
        List<OrderLineRequest> requests = List.of(new OrderLineRequest(socks.id(), 2),
                new OrderLineRequest(socks.id(), 3));

        // When
        UUID orderId = orderService.createOrder(requests, "test@gmail.com", "EUR");

        // Then
        Order order = orderRepository.findById(orderId).orElseThrow();
        assertThat(order)
            .hasLinesCount(1)
            .hasLine(socks, 5).hasTotal("50", "EUR");
    }

    @Test
    void shouldThrowExceptionWhenCartIsEmpty() {
        // Given
        List<OrderLineRequest> requests = List.of();

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(requests, "test@gmail.com", "PLN"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExist() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // And
        List<OrderLineRequest> requests = List.of(new OrderLineRequest(nonExistentId, 1));

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(requests, "test@gmail.com", "PLN"))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("not found");
    }

    @Test
    void shouldSendConfirmationEmail() {
        // Given
        Product product = persisted(productPricedAt(new Money("1000", "PLN")));

        // And
        List<OrderLineRequest> requests = List.of(new OrderLineRequest(product.id(), 2));

        // When
        orderService.createOrder(requests, "test@gmail.com", "PLN");

        // Then
        confirmationEmailWasSentTo("test@gmail.com");
    }

    private Product persisted(Product product) {
        return productRepository.save(product);
    }

    private void confirmationEmailWasSentTo(String email) {
        verify(emailService).sendEmail(email, "Your order has been created");
    }
}
