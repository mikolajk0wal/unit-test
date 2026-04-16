package com.mikolajk0wal.unittests;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    UUID id() {
        return id;
    }

    String name() {
        return name;
    }

    Money price() {
        return price;
    }
}
