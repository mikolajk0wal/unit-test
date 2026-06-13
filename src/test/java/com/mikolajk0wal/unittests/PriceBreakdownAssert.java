package com.mikolajk0wal.unittests;

class PriceBreakdownAssert {
    private final PriceBreakdown actual;

    private PriceBreakdownAssert(PriceBreakdown actual) {
        this.actual = actual;
    }

    static PriceBreakdownAssert assertThat(PriceBreakdown actual) {
        org.assertj.core.api.Assertions.assertThat(actual).as("PriceBreakdown").isNotNull();
        return new PriceBreakdownAssert(actual);
    }

    PriceBreakdownAssert hasTotal(String amount, String currency) {
        Money expectedTotal = new Money(amount, currency);

        org.assertj.core.api.Assertions.assertThat(actual.total()).as("Total price in breakdown")
                .isEqualTo(expectedTotal);

        return this;
    }

    PriceBreakdownAssert hasLinesCount(int expectedSize) {
        org.assertj.core.api.Assertions.assertThat(actual.pricingLines()).as("Pricing lines count")
                .hasSize(expectedSize);

        return this;
    }

    PriceBreakdownAssert hasLine(Product product, String amount, String currency) {
        Money expectedPrice = new Money(amount, currency);

        org.assertj.core.api.Assertions.assertThat(actual.pricingLines())
                .as("Pricing line for product: %s", product.name()).containsEntry(product.id(), expectedPrice);

        return this;
    }
}
