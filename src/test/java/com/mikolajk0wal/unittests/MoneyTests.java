package com.mikolajk0wal.unittests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class MoneyTests {
    @Test
    void shouldCreateMoneyWithZeroValue() {
        Money zero = Money.zero("GBP");

        assertThat(zero.amount()).isEqualByComparingTo(new BigDecimal("0.00"));
        assertThat(zero.currency()).isEqualTo("GBP");
    }

    @Test
    void shouldCreateMoneyWithRoundedAmount() {
        Money money = new Money("10.555", "PLN");

        assertThat(money.amount()).isEqualByComparingTo(new BigDecimal("10.56"));
        assertThat(money.currency()).isEqualTo("PLN");
    }

    @ParameterizedTest(name = "[{index}] Adding {0} and {1} {2} should give {3} {2}")
    @CsvSource({ "10.50, 5.50, EUR, 16.00", "10.254, 5.126, PLN, 15.38", "0.00, 0.00, USD, 0.00",
            "-5.00, 10.00, CHF, 5.00", "1.99, 0.01, GBP, 2.00" })
    void shouldAddSameCurrencies(String amount1, String amount2, String currency, String expected) {
        Money m1 = new Money(amount1, currency);
        Money m2 = new Money(amount2, currency);

        Money result = m1.add(m2);

        assertThat(result.amount()).isEqualByComparingTo(new BigDecimal(expected));
        assertThat(result.currency()).isEqualTo(currency);
    }

    @Test
    void shouldNotAddDifferentCurrencies() {
        Money pln = new Money("10", "PLN");
        Money usd = new Money("10", "USD");

        assertThatThrownBy(() -> pln.add(usd)).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource({ "10.00, 2, 20.00", "10.00, 0.5, 5.00", "1.33, 1.5, 2.00" })
    void shouldMultiplyByRate(String amount, String rate, String expected) {
        Money money = new Money(amount, "USD");
        Money result = money.multiply(new BigDecimal(rate));

        assertThat(result.amount()).isEqualByComparingTo(new BigDecimal(expected));
    }
}
