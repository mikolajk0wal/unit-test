package com.mikolajk0wal.unittests;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

record CurrencyPair(String from, String to) {
}

class ExchangeRates {
    private final Map<CurrencyPair, BigDecimal> rates;

    ExchangeRates(String baseCurrency, Map<String, BigDecimal> baseRates) {
        this.rates = calculateRatesMap(baseCurrency, baseRates);
    }

    Money convert(Money amount, String targetCurrency) {
        if (amount.currency().equals(targetCurrency)) {
            return amount;
        }

        CurrencyPair pair = new CurrencyPair(amount.currency(), targetCurrency);
        BigDecimal rate = rates.get(pair);

        if (rate == null) {
            throw new IllegalArgumentException("No rate found for: " + amount.currency() + " -> " + targetCurrency);
        }

        return new Money(amount.amount().multiply(rate), targetCurrency);
    }

    private static Map<CurrencyPair, BigDecimal> calculateRatesMap(String base, Map<String, BigDecimal> rates) {
        Map<String, BigDecimal> baseToAll = Stream
                .concat(rates.entrySet().stream(), Stream.of(Map.entry(base, BigDecimal.ONE)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return baseToAll.keySet().stream().flatMap(source -> baseToAll.keySet().stream().map(target -> {
            BigDecimal rateSourceToBase = BigDecimal.ONE.divide(baseToAll.get(source), 10, RoundingMode.HALF_UP);
            BigDecimal rateBaseToTarget = baseToAll.get(target);
            BigDecimal finalRate = rateSourceToBase.multiply(rateBaseToTarget).setScale(10, RoundingMode.HALF_UP);

            return Map.entry(new CurrencyPair(source, target), finalRate);
        })).collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
