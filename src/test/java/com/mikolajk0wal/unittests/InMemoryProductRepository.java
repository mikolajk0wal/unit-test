package com.mikolajk0wal.unittests;

import java.util.UUID;

class InMemoryProductRepository extends BaseJpaInMemoryRepository<Product, UUID> implements ProductRepository {
    @Override
    protected UUID generateId() {
        return UUID.randomUUID();
    }
}
