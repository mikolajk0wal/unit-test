package com.mikolajk0wal.unittests;

class OrderAssert {
    private final Order actual;

    private OrderAssert(Order actual) {
        this.actual = actual;
    }

    static OrderAssert assertThat(Order actual) {
        org.assertj.core.api.Assertions.assertThat(actual).as("Order").isNotNull();
        return new OrderAssert(actual);
    }

    OrderAssert hasTotal(String amount, String currency) {
        Money expectedTotal = new Money(amount, currency);

        org.assertj.core.api.Assertions.assertThat(actual.totalPrice()).as("Total price of order")
                .isEqualTo(expectedTotal);

        return this;
    }

    OrderAssert hasLinesCount(int expectedSize) {
        org.assertj.core.api.Assertions.assertThat(actual.lines()).as("Order lines count").hasSize(expectedSize);

        return this;
    }

    OrderAssert hasLine(Product product, int expectedQuantity) {
        boolean exists = actual.lines().stream()
                .anyMatch(line -> line.productId().equals(product.id()) && line.quantity() == expectedQuantity);

        org.assertj.core.api.Assertions.assertThat(exists)
                .as("Order contains product: %s with quantity: %d", product.name(), expectedQuantity).isTrue();

        return this;
    }
}
