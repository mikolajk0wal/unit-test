package com.mikolajk0wal.unittests;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

class PriceCalculator {
    PriceBreakdown calculate(PricingContext pricingContext) {
        if (pricingContext.productsWithQuantities().values().stream().anyMatch(q -> q < 0)) {
            throw new IllegalArgumentException("Quantity of product can't be less than zero");
        }

        BigDecimal discountMultiplier = isHappyHour(pricingContext.time()) ? new BigDecimal("0.90") : BigDecimal.ONE;

        Map<UUID, Money> pricingLines = pricingContext.productsWithQuantities().entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().id(),
                        entry -> pricingContext.exchangeRates()
                                .convert(entry.getKey().price(), pricingContext.targetCurrency())
                                .multiply(BigDecimal.valueOf(entry.getValue())).multiply(discountMultiplier)));

        Money total = pricingLines.values().stream().reduce(Money.zero(pricingContext.targetCurrency()), Money::add);

        return new PriceBreakdown(total, pricingLines);
    }

    private boolean isHappyHour(LocalDateTime time) {
        return time.getDayOfWeek() == DayOfWeek.FRIDAY && time.getHour() == 20;
    }
}
