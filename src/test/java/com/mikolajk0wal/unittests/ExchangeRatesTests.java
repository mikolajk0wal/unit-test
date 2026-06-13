package com.mikolajk0wal.unittests;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class ExchangeRatesTests {
    @Test
    void shouldConvertFromBaseCurrency() {
        ExchangeRates rates = new ExchangeRates("PLN", Map.of("EUR", new BigDecimal("0.25")));
        Money hundredPLN = new Money("100.00", "PLN");

        Money result = rates.convert(hundredPLN, "EUR");

        assertThat(result).isEqualTo(new Money("25.00", "EUR"));
    }

    @Test
    void shouldConvertToBaseCurrency() {
        ExchangeRates rates = new ExchangeRates("PLN", Map.of("EUR", new BigDecimal("0.25")));
        Money hundredEUR = new Money("100.00", "EUR");

        Money result = rates.convert(hundredEUR, "PLN");

        assertThat(result).isEqualTo(new Money("400.00", "PLN"));
    }

    @Test
    void shouldConvertBetweenNonBaseCurrencies() {
        ExchangeRates rates = new ExchangeRates("PLN",
                Map.of("USD", new BigDecimal("0.25"), "EUR", new BigDecimal("0.20")));
        Money hundredUSD = new Money("100.00", "USD");

        Money result = rates.convert(hundredUSD, "EUR");

        assertThat(result).isEqualTo(new Money("80.00", "EUR"));
    }

    @Test
    void shouldConvertToSameCurrency() {
        ExchangeRates rates = new ExchangeRates("PLN", Map.of("EUR", new BigDecimal("0.25")));
        Money hundredPLN = new Money("100.00", "PLN");

        Money result = rates.convert(hundredPLN, "PLN");

        assertThat(result).isEqualTo(new Money("100.00", "PLN"));
    }

    @Test
    void shouldMaintainPrecisionDuringCrossConversion() {
        ExchangeRates rates = new ExchangeRates("USD", Map.of("PLN", new BigDecimal("3")));
        Money hundredPLN = new Money("200.00", "PLN");

        Money result = rates.convert(hundredPLN, "USD");

        assertThat(result).isEqualTo(new Money("66.67", "USD"));
    }

    @Test
    void shouldThrowExceptionWhenSourceCurrencyNotFound() {
        ExchangeRates rates = new ExchangeRates("PLN", Map.of("EUR", new BigDecimal("0.25")));
        Money hundredJPY = new Money("100.00", "JPY");

        assertThatThrownBy(() -> rates.convert(hundredJPY, "PLN")).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("JPY");
    }

    @Test
    void shouldThrowExceptionWhenTargetCurrencyNotFound() {
        ExchangeRates rates = new ExchangeRates("PLN", Map.of("EUR", new BigDecimal("0.25")));
        Money hundredPLN = new Money("100.00", "PLN");

        assertThatThrownBy(() -> rates.convert(hundredPLN, "JPY")).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("JPY");
    }

    @Test
    void shouldAllowEmptyRatesMapForBaseCurrencyConversionsOnly() {
        ExchangeRates rates = new ExchangeRates("PLN", Map.of());
        Money hundredPLN = new Money("100.00", "PLN");

        Money result = rates.convert(hundredPLN, "PLN");

        assertThat(result).isEqualTo(new Money("100.00", "PLN"));
    }

    @Test
    void shouldThrowExceptionWhenBaseCurrencyIsInRatesMap() {
        assertThatThrownBy(() -> new ExchangeRates("PLN", Map.of("PLN", new BigDecimal("1.00"))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowExceptionWhenRateIsZero() {
        assertThatThrownBy(() -> new ExchangeRates("PLN", Map.of("EUR", BigDecimal.ZERO)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowExceptionWhenRateIsNegative() {
        assertThatThrownBy(() -> new ExchangeRates("PLN", Map.of("EUR", new BigDecimal("-0.25"))))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
