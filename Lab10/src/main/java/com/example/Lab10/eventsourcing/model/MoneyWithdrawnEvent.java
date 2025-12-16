package com.example.Lab10.eventsourcing.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MoneyWithdrawn")
public class MoneyWithdrawnEvent extends Event {
    private Double amount;

    public MoneyWithdrawnEvent() {}

    public MoneyWithdrawnEvent(String aggregateId, Long version, Double amount) {
        super(aggregateId, version);
        this.amount = amount;
    }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}




