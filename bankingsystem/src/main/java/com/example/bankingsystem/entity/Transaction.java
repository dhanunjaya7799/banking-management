package com.example.bankingsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "transaction_id", unique = true, nullable = false)
    private String transactionId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    @Column(nullable = false, precision = 15, scale = 2, columnDefinition = "DECIMAL(15,2)")
    private BigDecimal amount;
    
    @Column(name = "description")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;
    
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id")
    @JsonIgnoreProperties({"user", "outgoingTransactions", "incomingTransactions", "hibernateLazyInitializer", "handler"})
    private Account fromAccount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id")
    @JsonIgnoreProperties({"user", "outgoingTransactions", "incomingTransactions", "hibernateLazyInitializer", "handler"})
    private Account toAccount;
    
    @Column(name = "recipient_phone")
    private String recipientPhone;
    
    @Column(name = "recipient_account_number")
    private String recipientAccountNumber;
    
    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER, PAYMENT
    }
    
    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED, CANCELLED
    }
    
    @PrePersist
    protected void onCreate() {
        transactionDate = LocalDateTime.now();
        if (transactionId == null) {
            generateTransactionId();
        }
    }
    
    private void generateTransactionId() {
        this.transactionId = "TXN" + System.currentTimeMillis() + 
                           String.format("%04d", (int)(Math.random() * 10000));
    }
    
    // Constructors
    public Transaction() {}
    
    public Transaction(TransactionType transactionType, BigDecimal amount, 
                      Account fromAccount, Account toAccount, String description) {
        this.transactionType = transactionType;
        this.amount = amount;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public TransactionType getTransactionType() {
        return transactionType;
    }
    
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public TransactionStatus getStatus() {
        return status;
    }
    
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
    
    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    public Account getFromAccount() {
        return fromAccount;
    }
    
    public void setFromAccount(Account fromAccount) {
        this.fromAccount = fromAccount;
    }
    
    public Account getToAccount() {
        return toAccount;
    }
    
    public void setToAccount(Account toAccount) {
        this.toAccount = toAccount;
    }
    
    public String getRecipientPhone() {
        return recipientPhone;
    }
    
    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }
    
    public String getRecipientAccountNumber() {
        return recipientAccountNumber;
    }
    
    public void setRecipientAccountNumber(String recipientAccountNumber) {
        this.recipientAccountNumber = recipientAccountNumber;
    }
}
