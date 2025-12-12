package com.example.bankingsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;
    
    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhar number must be 12 digits")
    @Column(name = "aadhar_number", unique = true, nullable = false)
    private String aadharNumber;
    
    @Column(name = "aadhar_image_path")
    private String aadharImagePath;
    
    @Column(name = "date_of_birth")
    private String dateOfBirth;
    
    @NotBlank(message = "Address is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.CUSTOMER;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "transfer_pin")
    private String transferPin;
    
    @Column(name = "pin_created_at")
    private LocalDateTime pinCreatedAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"user", "transactions", "hibernateLazyInitializer", "handler"})
    private List<Account> accounts;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"user", "processedByStaff", "createdAccount", "hibernateLazyInitializer", "handler"})
    private List<AccountRequest> accountRequests;
    
    public enum UserRole {
        CUSTOMER, STAFF, ADMIN
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public User() {}
    
    public User(String firstName, String lastName, String email, String phoneNumber, 
                String aadharNumber, String dateOfBirth, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.aadharNumber = aadharNumber;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getAadharNumber() {
        return aadharNumber;
    }
    
    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }
    
    public String getAadharImagePath() {
        return aadharImagePath;
    }
    
    public void setAadharImagePath(String aadharImagePath) {
        this.aadharImagePath = aadharImagePath;
    }
    
    public String getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
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
    
    public String getTransferPin() {
        return transferPin;
    }
    
    public void setTransferPin(String transferPin) {
        this.transferPin = transferPin;
    }
    
    public LocalDateTime getPinCreatedAt() {
        return pinCreatedAt;
    }
    
    public void setPinCreatedAt(LocalDateTime pinCreatedAt) {
        this.pinCreatedAt = pinCreatedAt;
    }
    
    public List<Account> getAccounts() {
        return accounts;
    }
    
    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
    
    public List<AccountRequest> getAccountRequests() {
        return accountRequests;
    }
    
    public void setAccountRequests(List<AccountRequest> accountRequests) {
        this.accountRequests = accountRequests;
    }
}
