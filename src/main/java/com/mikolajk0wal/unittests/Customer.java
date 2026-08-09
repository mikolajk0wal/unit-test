package com.mikolajk0wal.unittests;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "customer")
class Customer {
    @Id
    private UUID id;
    private String email;
    private CustomerLevel level;
    private int completedOrders;

    protected Customer() {}

    public Customer(String email) {
        this(email, 0, CustomerLevel.BRONZE);
    }

    public Customer(String email, int completedOrders, CustomerLevel level) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.completedOrders = completedOrders;
        this.level = level;
    }

    public void registerNewOrder() {
        this.completedOrders++;
        recalculateLevel();
    }

    public void promoteTo(CustomerLevel newLevel) {
        if (newLevel.ordinal() > this.level.ordinal()) {
            this.level = newLevel;
        }
    }

    private void recalculateLevel() {
        if (this.completedOrders >= 15) {
            promoteTo(CustomerLevel.GOLD);
        } else if (this.completedOrders >= 5) {
            promoteTo(CustomerLevel.SILVER);
        }
    }

    UUID id() { return id; }
    String email() { return email; }
    CustomerLevel level() { return level; }
    int completedOrders() { return completedOrders; }
}

enum CustomerLevel {
    BRONZE, SILVER, GOLD
}