package com.mikolajk0wal.unittests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class OrderServiceTests {
    private ProductRepository productRepository;
    private OrderRepository orderRepository;
    private OrderService orderService;

    @BeforeEach
    void setup() {
        this.productRepository = new InMemoryProductRepository();
        this.orderRepository = new InMemoryOrderRepository();
        PriceCalculator priceCalculator = new PriceCalculator();

        this.orderService = new OrderService(productRepository, orderRepository, priceCalculator);
    }

    @Test
    void shouldCreateOrderWithCorrectTotalAndLines() {
        UUID productId = UUID.randomUUID();
        productRepository.save(new Product(productId, "Laptop", new BigDecimal("3000")));

        List<OrderLineRequest> requests = List.of(new OrderLineRequest(productId, 2));

        UUID orderId = orderService.createOrder(requests);

        Order savedOrder = orderRepository.findById(orderId).orElseThrow();

        assertThat(savedOrder.getTotalPrice()).isEqualByComparingTo(new BigDecimal("6000"));
        assertThat(savedOrder.getLines()).hasSize(1);
        assertThat(savedOrder.getLines().get(0).linePrice()).isEqualByComparingTo(new BigDecimal("6000"));
        assertThat(savedOrder.getLines().get(0).productId()).isEqualTo(productId);
    }
}
