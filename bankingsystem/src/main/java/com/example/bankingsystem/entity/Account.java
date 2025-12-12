package com.example.bankingsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Entity
@Table(name = "accounts")
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;
    
    @DecimalMin(value = "0.0", message = "Balance must be non-negative")
    @Column(nullable = false, precision = 15, scale = 2, columnDefinition = "DECIMAL(15,2) DEFAULT 0.00")
    private BigDecimal balance = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"accounts", "accountRequests", "hibernateLazyInitializer", "handler"})
    private User user;
    
    @OneToMany(mappedBy = "fromAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"fromAccount", "toAccount", "hibernateLazyInitializer", "handler"})
    private List<Transaction> outgoingTransactions;
    
    @OneToMany(mappedBy = "toAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"fromAccount", "toAccount", "hibernateLazyInitializer", "handler"})
    private List<Transaction> incomingTransactions;
    
    public enum AccountType {
        SAVINGS, CURRENT, FIXED_DEPOSIT
    }
    
    public enum AccountStatus {
        ACTIVE, INACTIVE, BLOCKED, CLOSED
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (accountNumber == null) {
            generateAccountNumber();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    private void generateAccountNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        this.accountNumber = sb.toString();
    }
    
    // Constructors
    public Account() {}
    
    public Account(AccountType accountType, BigDecimal initialBalance, User user) {
        this.accountType = accountType;
        this.balance = initialBalance;
        this.user = user;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public AccountType getAccountType() {
        return accountType;
    }
    
    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    public AccountStatus getStatus() {
        return status;
    }
    
    public void setStatus(AccountStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public List<Transaction> getOutgoingTransactions() {
        return outgoingTransactions;
    }
    
    public void setOutgoingTransactions(List<Transaction> outgoingTransactions) {
        this.outgoingTransactions = outgoingTransactions;
    }
    
    public List<Transaction> getIncomingTransactions() {
        return incomingTransactions;
    }
    
    public void setIncomingTransactions(List<Transaction> incomingTransactions) {
        this.incomingTransactions = incomingTransactions;
    }
}
