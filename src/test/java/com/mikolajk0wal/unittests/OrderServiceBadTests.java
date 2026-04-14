package com.mikolajk0wal.unittests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceBadTests {

    @Mock private ProductRepository productRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private PriceCalculator priceCalculator;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldCreateOrderSuccessfully() {
        // GIVEN
        UUID productId = UUID.randomUUID();
        Product mockProduct = new Product(productId, "Laptop", new BigDecimal("3000"));

        when(productRepository.findAllById(anyList())).thenReturn(List.of(mockProduct));

        // TU JEST PROBLEM: Wycinamy logikę kalkulatora i sami podajemy wynik
        PriceBreakdown mockBreakdown = new PriceBreakdown(new BigDecimal("6000"), Map.of(productId, new BigDecimal("6000")));
        when(priceCalculator.calculate(anyMap())).thenReturn(mockBreakdown);

        UUID expectedOrderId = UUID.randomUUID();
        Order expectedOrder = new Order(expectedOrderId, List.of(), new BigDecimal("6000"));
        when(orderRepository.save(any(Order.class))).thenReturn(expectedOrder);

        UUID orderId = orderService.createOrder(List.of(new OrderLineRequest(productId, 2)));

        verify(orderRepository).save(any(Order.class));
        assertEquals(expectedOrderId, orderId);
    }
}
