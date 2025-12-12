package com.example.bankingsystem.controller;

import com.example.bankingsystem.entity.User;
import com.example.bankingsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            String phoneNumber = loginRequest.getPhoneNumber();
            String role = loginRequest.getRole();
            String password = loginRequest.getPassword();
            
            Optional<User> userOpt = userService.getUserByPhoneNumber(phoneNumber);
            if (userOpt.isEmpty()) {
                return new ResponseEntity<>("User not found with this phone number", HttpStatus.NOT_FOUND);
            }
            
            User user = userOpt.get();
            if (!user.getRole().toString().equals(role)) {
                return new ResponseEntity<>("Invalid role selected for this user", HttpStatus.BAD_REQUEST);
            }
            if (user.getPassword() == null || !user.getPassword().equals(password)) {
                return new ResponseEntity<>("Invalid password", HttpStatus.UNAUTHORIZED);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("user", user);
            response.put("authenticated", true);
            
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    // Request DTOs
    public static class LoginRequest {
        private String phoneNumber;
        private String role;
        private String password;
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
