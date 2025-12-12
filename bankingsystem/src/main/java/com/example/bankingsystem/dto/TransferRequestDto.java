package com.example.bankingsystem.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class TransferRequestDto {
    
    @NotBlank(message = "From account number is required")
    private String fromAccountNumber;
    
    @NotBlank(message = "To identifier is required")
    private String toIdentifier;
    
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    
    private String description;
    
    private boolean isPhoneNumber = false;
    
    // Constructors
    public TransferRequestDto() {}
    
    public TransferRequestDto(String fromAccountNumber, String toIdentifier, 
                            BigDecimal amount, String description, boolean isPhoneNumber) {
        this.fromAccountNumber = fromAccountNumber;
        this.toIdentifier = toIdentifier;
        this.amount = amount;
        this.description = description;
        this.isPhoneNumber = isPhoneNumber;
    }
    
    // Getters and Setters
    public String getFromAccountNumber() {
        return fromAccountNumber;
    }
    
    public void setFromAccountNumber(String fromAccountNumber) {
        this.fromAccountNumber = fromAccountNumber;
    }
    
    public String getToIdentifier() {
        return toIdentifier;
    }
    
    public void setToIdentifier(String toIdentifier) {
        this.toIdentifier = toIdentifier;
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
    
    public boolean isPhoneNumber() {
        return isPhoneNumber;
    }
    
    public void setPhoneNumber(boolean phoneNumber) {
        isPhoneNumber = phoneNumber;
    }
}
