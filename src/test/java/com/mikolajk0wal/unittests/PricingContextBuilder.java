package com.mikolajk0wal.unittests;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

class PricingContextBuilder {
    private final Map<Product, Integer> products = new HashMap<>();
    private ExchangeRates rates = new ExchangeRates("PLN", Map.of());
    private String targetCurrency = "PLN";
    private LocalDateTime time = LocalDateTime.of(2026, 6, 8, 12, 0);

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

    PricingContextBuilder atTime(LocalDateTime time) {
        this.time = time;
        return this;
    }

    PricingContext build() {
        return new PricingContext(products, rates, targetCurrency, time);
    }
}
