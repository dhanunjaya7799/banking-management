package com.example.bankingsystem.dto;

import jakarta.validation.constraints.*;

public class UserRegistrationDto {
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;
    
    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhar number must be 12 digits")
    private String aadharNumber;
    
    private String aadharImagePath;
    
    @NotBlank(message = "Date of birth is required")
    private String dateOfBirth;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    // Constructors
    public UserRegistrationDto() {}
    
    public UserRegistrationDto(String firstName, String lastName, String email, 
                             String phoneNumber, String aadharNumber, 
                             String dateOfBirth, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.aadharNumber = aadharNumber;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }
    
    // Getters and Setters
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
}
