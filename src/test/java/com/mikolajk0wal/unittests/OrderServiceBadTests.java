package com.mikolajk0wal.unittests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceBadTests {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PriceCalculator priceCalculator;
    @Mock
    private EmailService emailService;
    @Mock
    private ExchangeRateProvider exchangeRateProvider;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldCreateOrderSuccessfully() {
        Product product = new Product("Product", new Money("4000", "PLN"));
        when(productRepository.findAllById(List.of(product.id()))).thenReturn(List.of(product));

        ExchangeRates exchangeRates = new ExchangeRates("PLN",
                Map.of("EUR", new BigDecimal("0.25"), "GBP", new BigDecimal("0.20"), "CHF", new BigDecimal("0.22")));
        when(exchangeRateProvider.getExchangeRates()).thenReturn(exchangeRates);

        PriceBreakdown breakdown = new PriceBreakdown(new Money("8000", "PLN"),
                Map.of(product.id(), new Money("8000", "PLN")));
        when(priceCalculator.calculate(new PricingContext(Map.of(product, 2), exchangeRates, "PLN")))
                .thenReturn(breakdown);

        Order order = new Order(List.of(new OrderLine(product.id(), 2, new Money("8000", "PLN"))),
                new Money("8000", "PLN"));
        when(orderRepository.save(order)).thenReturn(order);

        UUID orderId = orderService.createOrder(List.of(new OrderLineRequest(product.id(), 2)), "test@gmail.com",
                "PLN");

        verify(orderRepository).save(order);
        verify(emailService).sendEmail("test@gmail.com", "Your order has been created");
        assertEquals(order.getId(), orderId);
    }
}
