package com.mikolajk0wal.unittests;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

record ExchangeRates(Map<String, Map<String, BigDecimal>> rates) {
    ExchangeRates(String baseCurrency, Map<String, BigDecimal> baseRates) {
        this(calculateFullMap(baseCurrency, baseRates));
    }

    Money convert(Money amount, String targetCurrency) {
        return new Money(amount.multiply(rates.get(amount.currency()).get(targetCurrency)).amount(), targetCurrency);
    }

    /**
     * Tworzy mapę kursów na podstawie waluty bazowej. Przykład: dla "PLN" i mapy {"EUR": 4.30} wyliczy automatycznie
     * kurs {"EUR": {"PLN": 0.2326}}.
     */
    private static Map<String, Map<String, BigDecimal>> calculateFullMap(String base, Map<String, BigDecimal> rates) {
        List<Map.Entry<String, BigDecimal>> filteredRates = rates.entrySet().stream()
                .filter(entry -> !entry.getKey().equalsIgnoreCase(base)).toList();

        Map<String, BigDecimal> baseInnerMap = Stream
                .concat(filteredRates.stream(),
                        Stream.of(Map.entry(base, BigDecimal.ONE.setScale(4, RoundingMode.HALF_UP))))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

        return Stream
                .concat(Stream.of(Map.entry(base, baseInnerMap)),
                        filteredRates.stream()
                                .map(entry -> Map.entry(entry.getKey(),
                                        Map.of(base, BigDecimal.ONE.divide(entry.getValue(), 10, RoundingMode.HALF_UP),
                                                entry.getKey(), BigDecimal.ONE.setScale(4, RoundingMode.HALF_UP)))))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
