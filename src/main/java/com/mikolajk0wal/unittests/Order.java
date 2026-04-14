package com.mikolajk0wal.unittests;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
class Order {
    @Id
    private UUID id;

    @Column(name = "lines", columnDefinition = "jsonb") // W uproszczeniu, normalnie @OneToMany
    private List<OrderLine> lines;

    private BigDecimal totalPrice;

    protected Order() {}

    Order(UUID id, List<OrderLine> lines, BigDecimal totalPrice) {
        this.id = id;
        this.lines = lines;
        this.totalPrice = totalPrice;
    }

    UUID getId() { return id; }
    List<OrderLine> getLines() { return lines; }
    BigDecimal getTotalPrice() { return totalPrice; }
}

record OrderLine(
        UUID productId,
        String name,
        int quantity,
        BigDecimal linePrice
) {}
