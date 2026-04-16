package com.mikolajk0wal.unittests;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class Order {
    @Id
    private UUID id;

    @Column(name = "lines", columnDefinition = "jsonb")
    // @Type(JsonBinaryType.class)
    private List<OrderLine> lines;

    @Embedded
    private Money totalPrice;

    Order(List<OrderLine> lines, Money totalPrice) {
        this.lines = lines;
        this.totalPrice = totalPrice;
    }

    UUID getId() {
        return id;
    }

    List<OrderLine> getLines() {
        return lines;
    }

    Money getTotalPrice() {
        return totalPrice;
    }
}

record OrderLine(UUID productId, int quantity, Money linePrice) {
}
