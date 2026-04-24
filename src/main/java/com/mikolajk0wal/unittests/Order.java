package com.mikolajk0wal.unittests;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "orders")
class Order {
    @Id
    private UUID id;

    @Column(name = "lines", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<OrderLine> lines;

    @Embedded
    private Money totalPrice;

    Order(List<OrderLine> lines, Money totalPrice) {
        this.lines = lines;
        this.totalPrice = totalPrice;
    }

    protected Order() {
    }

    UUID id() {
        return id;
    }


    Money totalPrice() {
        return totalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

record OrderLine(UUID productId, int quantity, Money linePrice) {
}
