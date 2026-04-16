package com.mikolajk0wal.unittests;

import java.util.Map;

public record PricingContext(Map<Product, Integer> productsWithQuantities, ExchangeRates exchangeRates,
        String targetCurrency) {
}
