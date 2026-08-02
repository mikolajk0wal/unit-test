package com.mikolajk0wal.unittests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class CustomerActivityTrackerTest {
    private CustomerRepository customerRepository;
    private CustomerActivityTracker tracker;

    @BeforeEach
    void setUp() {
        this.customerRepository = new InMemoryCustomerRepository();
        this.tracker = new CustomerActivityTracker(customerRepository);
    }

    @Test
    void shouldIncrementCompletedOrdersForExistingCustomer() {
        // Given
        String existingCustomerEmail = "existing@gmail.com";
        Customer existingCustomer = persisted(new Customer(existingCustomerEmail, 3, CustomerLevel.BRONZE));

        // When
        tracker.onOrderCreated(new OrderCreated(UUID.randomUUID(), existingCustomerEmail));

        // Then
        Customer updatedCustomer = customerRepository.findByEmail(existingCustomerEmail).orElseThrow();
        assertThat(updatedCustomer.completedOrders()).isEqualTo(4);
        assertThat(updatedCustomer.level()).isEqualTo(CustomerLevel.BRONZE);
    }

    @Test
    void shouldCreateNewBronzeCustomerWhenEmailIsUnknown() {
        // Given
        String newCustomerEmail = "new@gmail.com";

        // When
        tracker.onOrderCreated(new OrderCreated(UUID.randomUUID(), newCustomerEmail));

        // Then
        Customer savedCustomer = customerRepository.findByEmail(newCustomerEmail).orElseThrow();
        assertThat(savedCustomer.level()).isEqualTo(CustomerLevel.BRONZE);
        assertThat(savedCustomer.completedOrders()).isEqualTo(1);
    }

    private Customer persisted(Customer customer) {
        return customerRepository.save(customer);
    }
}