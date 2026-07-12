package com.mikolajk0wal.unittests;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class PricingContextBuilder {
    private final Map<Product, Integer> products = new HashMap<>();
    private ExchangeRates rates = new ExchangeRates("PLN", Map.of());
    private String targetCurrency = "PLN";
    private List<PercentageDiscount> discounts = new ArrayList<>();

    private PricingContextBuilder() {
    }

    static PricingContextBuilder aPricingContext() {
        return new PricingContextBuilder();
    }

    PricingContextBuilder withItem(Product product, int quantity) {
        this.products.put(product, quantity);
        return this;
    }

    PricingContextBuilder usingRates(ExchangeRates rates) {
        this.rates = rates;
        return this;
    }

    PricingContextBuilder in(String currency) {
        this.targetCurrency = currency;
        return this;
    }

    PricingContextBuilder withDiscount(PercentageDiscount discount) {
        this.discounts.add(discount);
        return this;
    }

    PricingContext build() {
        return new PricingContext(products, rates, targetCurrency, discounts);
    }
}
