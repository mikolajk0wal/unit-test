package com.mikolajk0wal.unittests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    void shouldSendConfirmationEmail() {
        Product product = productRepository.save(new Product("Product", new Money("1000", "EUR")));
        List<OrderLineRequest> requests = List.of(new OrderLineRequest(product.id(), 2));

        orderService.createOrder(requests, "test@gmail.com", "PLN");

        verify(emailService).sendEmail("test@gmail.com", "Your order has been created");
    }
}
