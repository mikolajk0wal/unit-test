package com.mikolajk0wal.unittests;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
class Product {
    @Id
    private UUID id;
    private String name;
    private BigDecimal price;

    UUID id() { return id; }
    String name() { return name; }
    BigDecimal price() { return price; }
}