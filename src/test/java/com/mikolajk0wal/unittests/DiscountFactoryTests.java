package com.mikolajk0wal.unittests;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DiscountFactoryTests {
    private static final LocalDateTime REGULAR_TIME = LocalDateTime.of(2026, 7, 9, 12, 0);
    private static final LocalDateTime HAPPY_HOUR_TIME = LocalDateTime.of(2026, 7, 10, 20, 30);

    @Test
    void shouldReturnEmptyListForBronzeInRegularTime() {
        // Given
        DiscountFactory factory = new DiscountFactory();

        // When
        List<PercentageDiscount> discounts = factory.createDiscounts(CustomerLevel.BRONZE, REGULAR_TIME);

        // Then
        assertThat(discounts).isEmpty();
    }

    @Test
    void shouldReturnLevelDiscountForGoldInRegularTime() {
        // Given
        DiscountFactory factory = new DiscountFactory();

        // When
        List<PercentageDiscount> discounts = factory.createDiscounts(CustomerLevel.GOLD, REGULAR_TIME);

        // Then
        assertThat(discounts).containsExactly(new PercentageDiscount(new BigDecimal("0.10")));
    }

    @Test
    void shouldCombineHappyHourAndLevelDiscountForSilver() {
        // Given
        DiscountFactory factory = new DiscountFactory();

        // When
        List<PercentageDiscount> discounts = factory.createDiscounts(CustomerLevel.SILVER, HAPPY_HOUR_TIME);

        // Then
        assertThat(discounts).containsExactlyInAnyOrder(
                new PercentageDiscount(new BigDecimal("0.05")),
                new PercentageDiscount(new BigDecimal("0.10"))
        );
    }
}