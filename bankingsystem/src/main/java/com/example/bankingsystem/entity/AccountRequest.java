package com.example.bankingsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_requests")
public class AccountRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "request_id", unique = true, nullable = false)
    private String requestId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private Account.AccountType accountType;
    
    @DecimalMin(value = "0.0", message = "Initial deposit must be non-negative")
    @Column(name = "initial_deposit", precision = 15, scale = 2, columnDefinition = "DECIMAL(15,2) DEFAULT 0.00")
    private BigDecimal initialDeposit = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;
    
    @Column(name = "staff_comments")
    private String staffComments;
    
    @Column(name = "rejection_reason")
    private String rejectionReason;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"accounts", "accountRequests", "hibernateLazyInitializer", "handler"})
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by_staff_id")
    @JsonIgnoreProperties({"accounts", "accountRequests", "hibernateLazyInitializer", "handler"})
    private User processedByStaff;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "created_account_id")
    @JsonIgnoreProperties({"user", "transactions", "hibernateLazyInitializer", "handler"})
    private Account createdAccount;
    
    public enum RequestStatus {
        PENDING, APPROVED, REJECTED, UNDER_REVIEW
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (requestId == null) {
            generateRequestId();
        }
    }
    
    private void generateRequestId() {
        this.requestId = "REQ" + System.currentTimeMillis() + 
                        String.format("%04d", (int)(Math.random() * 10000));
    }
    
    // Constructors
    public AccountRequest() {}
    
    public AccountRequest(Account.AccountType accountType, BigDecimal initialDeposit, User user) {
        this.accountType = accountType;
        this.initialDeposit = initialDeposit;
        this.user = user;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public Account.AccountType getAccountType() {
        return accountType;
    }
    
    public void setAccountType(Account.AccountType accountType) {
        this.accountType = accountType;
    }
    
    public BigDecimal getInitialDeposit() {
        return initialDeposit;
    }
    
    public void setInitialDeposit(BigDecimal initialDeposit) {
        this.initialDeposit = initialDeposit;
    }
    
    public RequestStatus getStatus() {
        return status;
    }
    
    public void setStatus(RequestStatus status) {
        this.status = status;
    }
    
    public String getStaffComments() {
        return staffComments;
    }
    
    public void setStaffComments(String staffComments) {
        this.staffComments = staffComments;
    }
    
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
    
    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public User getProcessedByStaff() {
        return processedByStaff;
    }
    
    public void setProcessedByStaff(User processedByStaff) {
        this.processedByStaff = processedByStaff;
    }
    
    public Account getCreatedAccount() {
        return createdAccount;
    }
    
    public void setCreatedAccount(Account createdAccount) {
        this.createdAccount = createdAccount;
    }
}
