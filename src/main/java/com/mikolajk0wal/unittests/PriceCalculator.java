package com.mikolajk0wal.unittests;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
class PriceCalculator {
    PriceBreakdown calculate(PricingContext pricingContext) {
        Map<UUID, Money> pricingLines = pricingContext.productsWithQuantities().entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().id(),
                        entry -> pricingContext.exchangeRates()
                                .convert(entry.getKey().price(), pricingContext.targetCurrency())
                                .multiply(BigDecimal.valueOf(entry.getValue()))));

        Money total = pricingLines.values().stream().reduce(Money.zero(pricingContext.targetCurrency()), Money::add);

        return new PriceBreakdown(total, pricingLines);
    }
}
