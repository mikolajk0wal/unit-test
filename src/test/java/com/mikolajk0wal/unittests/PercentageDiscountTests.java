package com.mikolajk0wal.unittests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class PercentageDiscountTests {

    @Test
    void shouldCalculateDiscountForNormalPrice() {
        // Given
        PercentageDiscount discount = new PercentageDiscount(new BigDecimal("0.05"));

        // When
        Money result = discount.calculateDiscount(new Money("100.00", "PLN"));

        // Then
        assertThat(result).isEqualTo(new Money("5.00", "PLN"));
    }

    @ParameterizedTest
    @CsvSource({
            "0.00, 100.00",
            "0.05, 0.00",
            "0.00, 0.00"
    })
    void shouldReturnZeroForZeroValues(String percentage, String basePrice) {
        // Given
        PercentageDiscount discount = new PercentageDiscount(new BigDecimal(percentage));

        // When
        Money result = discount.calculateDiscount(new Money(basePrice, "PLN"));

        // Then
        assertThat(result).isEqualTo(new Money("0.00", "PLN"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-0.01", "-1.50", "1.01", "2.00"})
    void shouldThrowExceptionWhenPercentageIsOutOfBounds(String invalidPercentage) {
        // Expect
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new PercentageDiscount(new BigDecimal(invalidPercentage)));
    }
}