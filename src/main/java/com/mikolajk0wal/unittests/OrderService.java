package com.mikolajk0wal.unittests;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

class OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PriceCalculator priceCalculator;
    private final ExchangeRateProvider exchangeRateProvider;
    private final EmailService emailService;

    OrderService(ProductRepository productRepository, OrderRepository orderRepository, PriceCalculator priceCalculator,
            ExchangeRateProvider exchangeRateProvider, EmailService emailService) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.priceCalculator = priceCalculator;
        this.exchangeRateProvider = exchangeRateProvider;
        this.emailService = emailService;
    }

    UUID createOrder(List<OrderLineRequest> requests, String email, String currency) {
        List<UUID> productIds = requests.stream().map(OrderLineRequest::productId).toList();
        List<Product> products = productRepository.findAllById(productIds);

        Map<Product, Integer> productsWithQuantities = products.stream()
                .collect(Collectors.toMap(p -> p, p -> getQuantity(p.id(), requests)));

        ExchangeRates exchangeRates = exchangeRateProvider.getExchangeRates();
        PriceBreakdown breakdown = priceCalculator
                .calculate(new PricingContext(productsWithQuantities, exchangeRates, currency));

        List<OrderLine> lines = products.stream()
                .map(p -> new OrderLine(p.id(), getQuantity(p.id(), requests), breakdown.pricingLines().get(p.id())))
                .toList();

        Order order = new Order(lines, breakdown.total());
        orderRepository.save(order);

        emailService.sendEmail(email, "Your order has been created");
        return order.id();
    }

    private int getQuantity(UUID productId, List<OrderLineRequest> requests) {
        return requests.stream().filter(r -> r.productId().equals(productId)).findFirst()
                .map(OrderLineRequest::quantity).orElse(0);
    }
}
