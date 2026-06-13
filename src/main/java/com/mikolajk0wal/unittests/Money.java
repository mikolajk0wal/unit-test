package com.mikolajk0wal.unittests;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

record Money(BigDecimal amount, String currency) {

    Money {
        Objects.requireNonNull(amount);
        Objects.requireNonNull(currency);
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    Money(String amount, String currency) {
        this(new BigDecimal(amount), currency);
    }

    static Money zero(String currency) {
        return new Money("0", currency);
    }

    Money add(Money other) {
        Objects.requireNonNull(other);
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                    String.format("Cannot add different currencies: %s and %s", this.currency, other.currency));
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    Money multiply(BigDecimal rate) {
        Objects.requireNonNull(rate);
        return new Money(this.amount.multiply(rate), this.currency);
    }
}
