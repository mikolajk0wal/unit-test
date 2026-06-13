package com.mikolajk0wal.unittests;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

class OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PriceCalculator priceCalculator;
    private final ExchangeRateProvider exchangeRateProvider;
    private final EmailService emailService;
    private final Clock clock;

    OrderService(ProductRepository productRepository, OrderRepository orderRepository, PriceCalculator priceCalculator,
            ExchangeRateProvider exchangeRateProvider, EmailService emailService, Clock clock) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.priceCalculator = priceCalculator;
        this.exchangeRateProvider = exchangeRateProvider;
        this.emailService = emailService;
        this.clock = clock;
    }

    UUID createOrder(List<OrderLineRequest> requests, String email, String currency) {
        if (requests.isEmpty()) {
            throw new IllegalArgumentException("Cart cannot be empty");
        }

        Map<UUID, Integer> aggregatedQuantities = requests.stream().collect(
                Collectors.groupingBy(OrderLineRequest::productId, Collectors.summingInt(OrderLineRequest::quantity)));

        List<Product> products = productRepository.findAllById(aggregatedQuantities.keySet());

        if (products.size() != aggregatedQuantities.size()) {
            throw new IllegalArgumentException("One or more products not found");
        }

        Map<Product, Integer> productsWithQuantities = products.stream()
                .collect(Collectors.toMap(Function.identity(), p -> aggregatedQuantities.get(p.id())));

        ExchangeRates exchangeRates = exchangeRateProvider.getExchangeRates();
        PriceBreakdown breakdown = priceCalculator.calculate(
                new PricingContext(productsWithQuantities, exchangeRates, currency, LocalDateTime.now(clock)));

        List<OrderLine> lines = products.stream()
                .map(p -> new OrderLine(p.id(), productsWithQuantities.get(p), breakdown.pricingLines().get(p.id())))
                .toList();

        Order order = new Order(lines, breakdown.total());
        orderRepository.save(order);

        emailService.sendEmail(email, "Your order has been created");
        return order.id();
    }
}
