package com.mikolajk0wal.unittests;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
class DiscountFactory {

    List<PercentageDiscount> createDiscounts(CustomerLevel level, LocalDateTime time) {
        List<PercentageDiscount> discounts = new ArrayList<>();

        if (isHappyHour(time)) {
            discounts.add(new PercentageDiscount(new BigDecimal("0.10")));
        }

        switch (level) {
            case GOLD -> discounts.add(new PercentageDiscount(new BigDecimal("0.10")));
            case SILVER -> discounts.add(new PercentageDiscount(new BigDecimal("0.05")));
            case BRONZE -> {}
        }

        return Collections.unmodifiableList(discounts);
    }

    private boolean isHappyHour(LocalDateTime time) {
        return time.getDayOfWeek() == DayOfWeek.FRIDAY && time.getHour() == 20;
    }
}