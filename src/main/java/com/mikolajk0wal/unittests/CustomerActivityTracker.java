package com.mikolajk0wal.unittests;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
class CustomerActivityTracker {

    private final CustomerRepository customerRepository;

    CustomerActivityTracker(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Async
    @EventListener
    public void onOrderCreated(OrderCreated event) {
        Customer customer = customerRepository.findByEmail(event.customerEmail())
                .orElseGet(() -> new Customer(event.customerEmail()));

        customer.registerNewOrder();
        customerRepository.save(customer);
    }
}