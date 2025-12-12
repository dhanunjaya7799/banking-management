package com.example.bankingsystem.dto;

import com.example.bankingsystem.entity.Account;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class AccountRequestDto {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Account type is required")
    private Account.AccountType accountType;
    
    @DecimalMin(value = "0.0", message = "Initial deposit must be non-negative")
    private BigDecimal initialDeposit = BigDecimal.ZERO;
    
    // Constructors
    public AccountRequestDto() {}
    
    public AccountRequestDto(Long userId, Account.AccountType accountType, BigDecimal initialDeposit) {
        this.userId = userId;
        this.accountType = accountType;
        this.initialDeposit = initialDeposit;
    }
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
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
}
