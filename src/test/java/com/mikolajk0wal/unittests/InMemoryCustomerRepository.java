package com.mikolajk0wal.unittests;

import java.util.Optional;
import java.util.UUID;

class InMemoryCustomerRepository extends BaseJpaInMemoryRepository<Customer, UUID> implements CustomerRepository {
    @Override
    protected UUID generateId() {
        return UUID.randomUUID();
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return entities.values().stream()
                .filter(c -> c.email().equals(email))
                .findAny();
    }
}
