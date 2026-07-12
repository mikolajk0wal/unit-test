package com.mikolajk0wal.unittests;

import java.math.BigDecimal;

record PercentageDiscount(BigDecimal percentage) {
    PercentageDiscount {
        if (percentage.compareTo(BigDecimal.ZERO) < 0 || percentage.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Percentage must be between 0.0 and 1.0");
        }
    }

    public Money calculateDiscount(Money basePrice) {
        return basePrice.multiply(percentage);
    }
}