package com.mikolajk0wal.unittests;

import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

class OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PriceCalculator priceCalculator;
    private final ExchangeRateProvider exchangeRateProvider;
    private final CustomerRepository customerRepository;
    private final DiscountFactory discountFactory;
    private final EventPublisher eventPublisher;
    private final Clock clock;

    OrderService(ProductRepository productRepository, OrderRepository orderRepository, PriceCalculator priceCalculator,
                 ExchangeRateProvider exchangeRateProvider,
                 CustomerRepository customerRepository, DiscountFactory discountFactory, EventPublisher eventPublisher, Clock clock) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.priceCalculator = priceCalculator;
        this.exchangeRateProvider = exchangeRateProvider;
        this.customerRepository = customerRepository;
        this.discountFactory = discountFactory;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
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

        Customer customer = customerRepository.findByEmail(email)
                .orElseGet(() -> new Customer(email));

        List<PercentageDiscount> discounts = discountFactory.createDiscounts(customer.level(), LocalDateTime.now(clock));

        ExchangeRates exchangeRates = exchangeRateProvider.getExchangeRates();
        PriceBreakdown breakdown = priceCalculator.calculate(
                new PricingContext(productsWithQuantities, exchangeRates, currency, discounts));

        List<OrderLine> lines = products.stream()
                .map(p -> new OrderLine(p.id(), productsWithQuantities.get(p), breakdown.pricingLines().get(p.id())))
                .toList();

        Order order = new Order(lines, breakdown.total());
        orderRepository.save(order);

        eventPublisher.publish(new OrderCreated(order.id(), email));

        return order.id();
    }
}
