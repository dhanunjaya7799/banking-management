package com.example.bankingsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otps")
public class OTP {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    
    @Column(name = "otp_code", nullable = false)
    private String otpCode;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "is_used", nullable = false)
    private boolean isUsed = false;
    
    @Column(name = "attempts", nullable = false)
    private int attempts = 0;
    
    // Constructors
    public OTP() {}
    
    public OTP(String phoneNumber, String otpCode) {
        this.phoneNumber = phoneNumber;
        this.otpCode = otpCode;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusMinutes(5); // OTP expires in 5 minutes
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getOtpCode() {
        return otpCode;
    }
    
    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public boolean isUsed() {
        return isUsed;
    }
    
    public void setUsed(boolean used) {
        isUsed = used;
    }
    
    public int getAttempts() {
        return attempts;
    }
    
    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    public boolean isValid() {
        return !isUsed && !isExpired() && attempts < 3;
    }
}
