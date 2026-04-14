package com.mikolajk0wal.unittests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PriceCalculator priceCalculator;

    UUID createOrder(List<OrderLineRequest> requests) {
        List<UUID> productIds = requests.stream()
                .map(OrderLineRequest::productId)
                .toList();
        List<Product> products = productRepository.findAllById(productIds);

        Map<Product, Integer> productsWithQuantities = products.stream()
                .collect(Collectors.toMap(
                        p -> p,
                        p -> getQuantity(p.id(), requests)
                ));

        PriceBreakdown breakdown = priceCalculator.calculate(productsWithQuantities);

        List<OrderLine> lines = products.stream()
                .map(p -> new OrderLine(
                        p.id(),
                        p.name(),
                        getQuantity(p.id(), requests),
                        breakdown.pricingLines().get(p.id())
                )).toList();

        Order order = new Order(UUID.randomUUID(), lines, breakdown.total());
        orderRepository.save(order);

        return order.getId();
    }

    private int getQuantity(UUID productId, List<OrderLineRequest> requests) {
        return requests.stream()
                .filter(r -> r.productId().equals(productId))
                .findFirst()
                .map(OrderLineRequest::quantity)
                .orElse(0);
    }
}