package com.mikolajk0wal.unittests;

import java.util.UUID;

class InMemoryOrderRepository extends BaseJpaInMemoryRepository<Order, UUID> implements OrderRepository{
    @Override
    protected UUID generateId() {
        return UUID.randomUUID();
    }
}
