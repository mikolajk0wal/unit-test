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

    @ParameterizedTest(name = "[{index}] Amount {0} should be rounded to {1}")
    @CsvSource({ "10.555, 10.56", "10.556, 10.56", "10.554, 10.55" })
    void shouldCreateMoneyWithRoundedAmount(String input, String expected) {
        Money money = new Money(input, "PLN");

        assertThat(money.amount()).isEqualByComparingTo(new BigDecimal(expected));
        assertThat(money.currency()).isEqualTo("PLN");
    }

    @Test
    void shouldNormalizeScaleSoDifferentStringFormatsAreEqual() {
        Money m1 = new Money("10", "PLN");
        Money m2 = new Money("10.00", "PLN");

        assertThat(m1).isEqualTo(m2);
    }

    @ParameterizedTest(name = "[{index}] Adding {0} and {1} {2} should give {3} {2}")
    @CsvSource({ "10.50, 5.50, EUR, 16.00", "5.00, 0.00, USD, 5.00", "10.254, 5.124, PLN, 15.37",
            "10.00, -5.00, CHF, 5.00", })
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

    @ParameterizedTest(name = "[{index}] Multiplying {0} by {1} should equal {2}")
    @CsvSource({ "10.00, 2, 20.00", "1.33, 1.5, 2.00", "10.00, 0.5, 5.00", "10.00, 0, 0.00", "10.00, -2, -20.00" })
    void shouldMultiplyByRate(String amount, String rate, String expected) {
        Money money = new Money(amount, "USD");
        Money result = money.multiply(new BigDecimal(rate));

        assertThat(result.amount()).isEqualByComparingTo(new BigDecimal(expected));
        assertThat(result.currency()).isEqualTo("USD");
    }
}
