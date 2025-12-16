package com.example.Lab10.eventsourcing.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MoneyDeposited")
public class MoneyDepositedEvent extends Event {
    private Double amount;

    public MoneyDepositedEvent() {}

    public MoneyDepositedEvent(String aggregateId, Long version, Double amount) {
        super(aggregateId, version);
        this.amount = amount;
    }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}




