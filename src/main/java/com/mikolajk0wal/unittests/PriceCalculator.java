package com.mikolajk0wal.unittests;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
class PriceCalculator {
    PriceBreakdown calculate(Map<Product, Integer> productsWithQuantities) {
        Map<UUID, BigDecimal> pricingLines = productsWithQuantities.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().id(),
                        entry -> entry.getKey().price().multiply(BigDecimal.valueOf(entry.getValue()))
                ));

        BigDecimal total = pricingLines.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PriceBreakdown(total, pricingLines);
    }
}
