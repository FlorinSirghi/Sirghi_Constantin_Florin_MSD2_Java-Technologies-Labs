package com.example.Lab10.eventsourcing.service;

import com.example.Lab10.eventsourcing.model.*;
import com.example.Lab10.eventsourcing.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountService {
    
    private static final int SNAPSHOT_INTERVAL = 5; // Create snapshot every 5 events
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private AccountProjectionRepository projectionRepository;
    
    @Autowired
    private AccountSnapshotRepository snapshotRepository;

    // Create account - emits AccountCreatedEvent
    @Transactional
    public void createAccount(String accountId, String owner, Double initialBalance) {
        Long nextVersion = getNextVersion(accountId);
        AccountCreatedEvent event = new AccountCreatedEvent(accountId, nextVersion, accountId, owner, initialBalance);
        eventRepository.save(event);
        updateProjection(accountId);
        createSnapshotIfNeeded(accountId);
    }

    // Deposit money - emits MoneyDepositedEvent
    @Transactional
    public void deposit(String accountId, Double amount) {
        Long nextVersion = getNextVersion(accountId);
        MoneyDepositedEvent event = new MoneyDepositedEvent(accountId, nextVersion, amount);
        eventRepository.save(event);
        updateProjection(accountId);
        createSnapshotIfNeeded(accountId);
    }

    // Withdraw money - emits MoneyWithdrawnEvent
    @Transactional
    public void withdraw(String accountId, Double amount) {
        AccountProjection projection = projectionRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));
        
        if (projection.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds");
        }
        
        Long nextVersion = getNextVersion(accountId);
        MoneyWithdrawnEvent event = new MoneyWithdrawnEvent(accountId, nextVersion, amount);
        eventRepository.save(event);
        updateProjection(accountId);
        createSnapshotIfNeeded(accountId);
    }

    // Get account balance from projection (read-optimized view)
    public AccountProjection getAccount(String accountId) {
        return projectionRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public AccountProjection rebuildFromEvents(String accountId) {
        // Try to load from snapshot first
        AccountSnapshot snapshot = snapshotRepository.findFirstByAccountIdOrderBySnapshotVersionDesc(accountId)
            .orElse(null);
        
        Double balance = 0.0;
        String owner = null;
        Long startVersion = 0L;
        
        if (snapshot != null) {
            balance = snapshot.getBalance();
            owner = snapshot.getOwner();
            startVersion = snapshot.getSnapshotVersion();
        }
        
        // Replay events since snapshot
        List<Event> events = eventRepository.findByAggregateIdAndVersionGreaterThanOrderByVersionAsc(
            accountId, startVersion);
        
        for (Event event : events) {
            if (event instanceof AccountCreatedEvent) {
                AccountCreatedEvent e = (AccountCreatedEvent) event;
                owner = e.getOwner();
                balance = e.getInitialBalance();
            } else if (event instanceof MoneyDepositedEvent) {
                MoneyDepositedEvent e = (MoneyDepositedEvent) event;
                balance += e.getAmount();
            } else if (event instanceof MoneyWithdrawnEvent) {
                MoneyWithdrawnEvent e = (MoneyWithdrawnEvent) event;
                balance -= e.getAmount();
            }
        }
        
        return new AccountProjection(accountId, owner, balance, 
            events.isEmpty() ? startVersion : events.get(events.size() - 1).getVersion());
    }

    // Update projection from events
    private void updateProjection(String accountId) {
        AccountProjection projection = rebuildFromEvents(accountId);
        projectionRepository.save(projection);
    }

    // Create snapshot if threshold reached
    private void createSnapshotIfNeeded(String accountId) {
        List<Event> allEvents = eventRepository.findByAggregateIdOrderByVersionAsc(accountId);
        
        if (allEvents.size() % SNAPSHOT_INTERVAL == 0 && !allEvents.isEmpty()) {
            AccountProjection projection = rebuildFromEvents(accountId);
            AccountSnapshot snapshot = new AccountSnapshot(
                accountId,
                projection.getOwner(),
                projection.getBalance(),
                projection.getLastEventVersion()
            );
            snapshotRepository.save(snapshot);
        }
    }

    private Long getNextVersion(String aggregateId) {
        List<Event> events = eventRepository.findByAggregateIdOrderByVersionAsc(aggregateId);
        return events.isEmpty() ? 1L : events.get(events.size() - 1).getVersion() + 1;
    }
}




