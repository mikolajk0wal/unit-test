package com.mikolajk0wal.unittests;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

record CurrencyPair(String source, String target) {
}

class ExchangeRates {
    private final Map<CurrencyPair, BigDecimal> rates;

    ExchangeRates(String baseCurrency, Map<String, BigDecimal> baseRates) {
        if (baseRates.containsKey(baseCurrency)) {
            throw new IllegalArgumentException("Base currency cannot be present in the rates map");
        }

        baseRates.forEach((currency, rate) -> {
            if (rate.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Exchange rate must be strictly positive, got: " + rate);
            }
        });

        this.rates = calculateRatesMap(baseCurrency, baseRates);
    }

    Money convert(Money money, String targetCurrency) {
        CurrencyPair pair = new CurrencyPair(money.currency(), targetCurrency);
        BigDecimal rate = rates.get(pair);

        if (rate == null) {
            throw new IllegalArgumentException("No rate found for: " + money.currency() + " -> " + targetCurrency);
        }

        return new Money(money.amount().multiply(rate), targetCurrency);
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
