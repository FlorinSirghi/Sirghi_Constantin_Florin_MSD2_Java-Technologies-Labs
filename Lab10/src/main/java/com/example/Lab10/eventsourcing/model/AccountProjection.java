package com.example.Lab10.eventsourcing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_projections")
public class AccountProjection {
    @Id
    private String accountId;
    
    private String owner;
    private Double balance;
    private Long lastEventVersion;
    private LocalDateTime lastUpdated;

    public AccountProjection() {}

    public AccountProjection(String accountId, String owner, Double balance, Long lastEventVersion) {
        this.accountId = accountId;
        this.owner = owner;
        this.balance = balance;
        this.lastEventVersion = lastEventVersion;
        this.lastUpdated = LocalDateTime.now();
    }

    // Getters and Setters
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    
    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }
    
    public Long getLastEventVersion() { return lastEventVersion; }
    public void setLastEventVersion(Long lastEventVersion) { this.lastEventVersion = lastEventVersion; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}




