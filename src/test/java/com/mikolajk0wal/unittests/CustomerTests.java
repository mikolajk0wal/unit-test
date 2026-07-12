package com.mikolajk0wal.unittests;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.IntStream;

class CustomerTests {

    @Test
    void shouldBeBronzeByDefaultForNewCustomer() {
        // Given
        Customer customer = new Customer("test@gmail.com");

        // Then
        assertThat(customer.level()).isEqualTo(CustomerLevel.BRONZE);
    }

    @Test
    void shouldPromoteToSilverAfterFiveOrders() {
        // Given
        Customer customer = new Customer("test@gmail.com");

        // When
        registerOrders(customer, 5);

        // Then
        assertThat(customer.level()).isEqualTo(CustomerLevel.SILVER);
    }

    @Test
    void shouldPromoteToGoldAfterFifteenOrders() {
        // Given
        Customer customer = new Customer("test@gmail.com");

        // When
        registerOrders(customer, 15);

        // Then
        assertThat(customer.level()).isEqualTo(CustomerLevel.GOLD);
    }

    @Test
    void shouldAllowManualPromotionToHigherLevel() {
        // Given
        Customer customer = new Customer("test@gmail.com");

        // When
        customer.promoteTo(CustomerLevel.GOLD);

        // Then
        assertThat(customer.level()).isEqualTo(CustomerLevel.GOLD);
    }

    @Test
    void shouldNotDowngradeLevelDuringManualPromotion() {
        // Given
        Customer customer = new Customer("test@gmail.com");
        customer.promoteTo(CustomerLevel.GOLD);

        // When
        customer.promoteTo(CustomerLevel.SILVER);

        // Then
        assertThat(customer.level()).isEqualTo(CustomerLevel.GOLD);
    }

    @Test
    void shouldNotDowngradeLevelWhenRegisteringOrderAfterManualPromotion() {
        // Given
        Customer customer = new Customer("test@gmail.com");
        customer.promoteTo(CustomerLevel.GOLD);

        // When
        customer.registerNewOrder();

        // Then
        assertThat(customer.level()).isEqualTo(CustomerLevel.GOLD);
    }

    private void registerOrders(Customer customer, int count) {
        IntStream.range(0, count).forEach(i -> customer.registerNewOrder());
    }
}