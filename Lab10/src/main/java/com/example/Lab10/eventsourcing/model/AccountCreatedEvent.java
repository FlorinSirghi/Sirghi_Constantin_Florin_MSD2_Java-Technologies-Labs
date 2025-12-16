package com.example.Lab10.eventsourcing.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("AccountCreated")
public class AccountCreatedEvent extends Event {
    private String accountId;
    private String owner;
    private Double initialBalance;

    public AccountCreatedEvent() {}

    public AccountCreatedEvent(String aggregateId, Long version, String accountId, String owner, Double initialBalance) {
        super(aggregateId, version);
        this.accountId = accountId;
        this.owner = owner;
        this.initialBalance = initialBalance;
    }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    
    public Double getInitialBalance() { return initialBalance; }
    public void setInitialBalance(Double initialBalance) { this.initialBalance = initialBalance; }
}




