package com.example.Lab10.eventsourcing.controller;

import com.example.Lab10.eventsourcing.model.AccountProjection;
import com.example.Lab10.eventsourcing.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/eventsourcing")
public class AccountController {
    
    @Autowired
    private AccountService accountService;

    @PostMapping("/accounts")
    public void createAccount(@RequestBody CreateAccountRequest request) {
        accountService.createAccount(request.getAccountId(), request.getOwner(), request.getInitialBalance());
    }

    @PostMapping("/accounts/{accountId}/deposit")
    public void deposit(@PathVariable String accountId, @RequestBody AmountRequest request) {
        accountService.deposit(accountId, request.getAmount());
    }

    @PostMapping("/accounts/{accountId}/withdraw")
    public void withdraw(@PathVariable String accountId, @RequestBody AmountRequest request) {
        accountService.withdraw(accountId, request.getAmount());
    }

    @GetMapping("/accounts/{accountId}")
    public AccountProjection getAccount(@PathVariable String accountId) {
        return accountService.getAccount(accountId);
    }

    @GetMapping("/accounts/{accountId}/rebuild")
    public AccountProjection rebuildAccount(@PathVariable String accountId) {
        return accountService.rebuildFromEvents(accountId);
    }

    public static class CreateAccountRequest {
        private String accountId;
        private String owner;
        private Double initialBalance;

        public String getAccountId() { return accountId; }
        public void setAccountId(String accountId) { this.accountId = accountId; }
        
        public String getOwner() { return owner; }
        public void setOwner(String owner) { this.owner = owner; }
        
        public Double getInitialBalance() { return initialBalance; }
        public void setInitialBalance(Double initialBalance) { this.initialBalance = initialBalance; }
    }

    public static class AmountRequest {
        private Double amount;

        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
    }
}




