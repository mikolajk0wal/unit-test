package com.mikolajk0wal.unittests;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "products")
class Product {
    @Id
    private UUID id;
    private String name;

    @Embedded
    private Money price;

    public Product(String name, Money price) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.price = price;
    }

    protected Product() {
    }

    UUID id() {
        return id;
    }

    Money price() {
        return price;
    }
}
