package com.example.Lab10.eventsourcing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_snapshots")
public class AccountSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String accountId;
    private String owner;
    private Double balance;
    private Long snapshotVersion;
    private LocalDateTime createdAt;

    public AccountSnapshot() {}

    public AccountSnapshot(String accountId, String owner, Double balance, Long snapshotVersion) {
        this.accountId = accountId;
        this.owner = owner;
        this.balance = balance;
        this.snapshotVersion = snapshotVersion;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    
    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }
    
    public Long getSnapshotVersion() { return snapshotVersion; }
    public void setSnapshotVersion(Long snapshotVersion) { this.snapshotVersion = snapshotVersion; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}




