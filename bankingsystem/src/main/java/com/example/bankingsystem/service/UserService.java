package com.example.bankingsystem.service;

import com.example.bankingsystem.entity.User;
import com.example.bankingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User createUser(User user) {
        validateUserData(user);
        return userRepository.save(user);
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }
    
    public Optional<User> getUserByAadharNumber(String aadharNumber) {
        return userRepository.findByAadharNumber(aadharNumber);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public List<User> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role);
    }
    
    public List<User> getAllStaff() {
        return userRepository.findAllStaff();
    }
    
    public User updateUser(Long id, User updatedUser) {
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            
            // Only allow updating phone number and email
            if (updatedUser.getPhoneNumber() != null && 
                !updatedUser.getPhoneNumber().equals(existingUser.getPhoneNumber())) {
                if (userRepository.existsByPhoneNumber(updatedUser.getPhoneNumber())) {
                    throw new RuntimeException("Phone number already exists");
                }
                existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            }
            
            if (updatedUser.getEmail() != null && 
                !updatedUser.getEmail().equals(existingUser.getEmail())) {
                if (userRepository.existsByEmail(updatedUser.getEmail())) {
                    throw new RuntimeException("Email already exists");
                }
                existingUser.setEmail(updatedUser.getEmail());
            }
            
            return userRepository.save(existingUser);
        }
        throw new RuntimeException("User not found with id: " + id);
    }
    
    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("User not found with id: " + id);
        }
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }
    
    public boolean existsByAadharNumber(String aadharNumber) {
        return userRepository.existsByAadharNumber(aadharNumber);
    }
    
    public List<User> searchUsersByName(String name) {
        return userRepository.findByNameContaining(name);
    }
    
    public User createTransferPin(Long userId, String pin) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Check if PIN already exists
            if (user.getTransferPin() != null) {
                throw new RuntimeException("Transfer PIN already exists. PIN can only be created once.");
            }
            
            // Validate PIN (6 digits)
            if (pin == null || !pin.matches("\\d{6}")) {
                throw new RuntimeException("PIN must be exactly 6 digits");
            }
            
            user.setTransferPin(pin);
            user.setPinCreatedAt(LocalDateTime.now());
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found with id: " + userId);
    }
    
    public boolean hasTransferPin(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.isPresent() && userOpt.get().getTransferPin() != null;
    }
    
    public boolean verifyTransferPin(Long userId, String pin) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return user.getTransferPin() != null && user.getTransferPin().equals(pin);
        }
        return false;
    }
    
    private void validateUserData(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists: " + user.getPhoneNumber());
        }
        
        if (userRepository.existsByAadharNumber(user.getAadharNumber())) {
            throw new RuntimeException("Aadhar number already exists: " + user.getAadharNumber());
        }
    }
}
