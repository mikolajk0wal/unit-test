package com.mikolajk0wal.unittests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OrderServiceTests {
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

        orderService = new OrderService(productRepository, orderRepository, priceCalculator, exchangeRateProvider,
                emailService);
    }

    @Test
    void shouldCreateOrderWithCorrectPrice() {
        Product product = productRepository.save(new Product("Product", new Money("1000", "EUR")));
        List<OrderLineRequest> requests = List.of(new OrderLineRequest(product.id(), 2));

        UUID orderId = orderService.createOrder(requests, "test@gmail.com", "PLN");

        Order savedOrder = orderRepository.findById(orderId).orElseThrow();
        assertThat(savedOrder.totalPrice().amount()).isEqualByComparingTo(new BigDecimal("8000"));
        assertThat(savedOrder.totalPrice().currency()).isEqualTo("PLN");
    }

    @Test
    void shouldCreateOrderWithMultipleDifferentProducts() {
        Product p1 = productRepository.save(new Product("Product 1", new Money("10.00", "PLN")));
        Product p2 = productRepository.save(new Product("Product 2", new Money("20.00", "PLN")));
        List<OrderLineRequest> requests = List.of(new OrderLineRequest(p1.id(), 1), new OrderLineRequest(p2.id(), 2));

        UUID orderId = orderService.createOrder(requests, "test@gmail.com", "PLN");

        Order savedOrder = orderRepository.findById(orderId).orElseThrow();
        assertThat(savedOrder.lines()).anyMatch(l -> l.productId().equals(p1.id()));
        assertThat(savedOrder.lines()).anyMatch(l -> l.productId().equals(p2.id()));
    }

    @Test
    void shouldMergeQuantitiesWhenSameProductAppearsInMultipleLines() {
        Product product = productRepository.save(new Product("Product", new Money("10.00", "PLN")));
        List<OrderLineRequest> requests = List.of(new OrderLineRequest(product.id(), 2),
                new OrderLineRequest(product.id(), 3));

        UUID orderId = orderService.createOrder(requests, "test@gmail.com", "PLN");

        Order savedOrder = orderRepository.findById(orderId).orElseThrow();
        assertThat(savedOrder.lines()).hasSize(1);
        assertThat(savedOrder.lines().get(0).quantity()).isEqualTo(5);
        assertThat(savedOrder.totalPrice()).isEqualTo(new Money("50", "PLN"));
    }

    @Test
    void shouldThrowExceptionWhenCartIsEmpty() {
        List<OrderLineRequest> requests = List.of();

        assertThatThrownBy(() -> orderService.createOrder(requests, "test@gmail.com", "PLN"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExist() {
        UUID nonExistentId = UUID.randomUUID();
        List<OrderLineRequest> requests = List.of(new OrderLineRequest(nonExistentId, 1));

        assertThatThrownBy(() -> orderService.createOrder(requests, "test@gmail.com", "PLN"))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("not found");
    }

    @Test
    void shouldSendConfirmationEmail() {
        Product product = productRepository.save(new Product("Product", new Money("1000", "EUR")));
        List<OrderLineRequest> requests = List.of(new OrderLineRequest(product.id(), 2));

        orderService.createOrder(requests, "test@gmail.com", "PLN");

        verify(emailService).sendEmail("test@gmail.com", "Your order has been created");
    }
}
