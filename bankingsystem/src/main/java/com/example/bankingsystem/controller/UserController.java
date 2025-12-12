package com.example.bankingsystem.controller;

import com.example.bankingsystem.entity.User;
import com.example.bankingsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/staff")
    public ResponseEntity<?> createStaff(@Valid @RequestBody User user) {
        try {
            // Force role to STAFF regardless of what client sends
            user.setRole(User.UserRole.STAFF);
            User createdStaff = userService.createUser(user);
            return new ResponseEntity<>(createdStaff, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            Optional<User> user = userService.getUserById(id);
            if (user.isPresent()) {
                return new ResponseEntity<>(user.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        try {
            Optional<User> user = userService.getUserByEmail(email);
            if (user.isPresent()) {
                return new ResponseEntity<>(user.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<?> getUserByPhoneNumber(@PathVariable String phoneNumber) {
        try {
            Optional<User> user = userService.getUserByPhoneNumber(phoneNumber);
            if (user.isPresent()) {
                return new ResponseEntity<>(user.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/aadhar/{aadharNumber}")
    public ResponseEntity<?> getUserByAadharNumber(@PathVariable String aadharNumber) {
        try {
            Optional<User> user = userService.getUserByAadharNumber(aadharNumber);
            if (user.isPresent()) {
                return new ResponseEntity<>(user.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/role/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        try {
            User.UserRole userRole = User.UserRole.valueOf(role.toUpperCase());
            List<User> users = userService.getUsersByRole(userRole);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid role: " + role, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/staff")
    public ResponseEntity<?> getAllStaff() {
        try {
            List<User> staff = userService.getAllStaff();
            return new ResponseEntity<>(staff, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchUsersByName(@RequestParam String name) {
        try {
            List<User> users = userService.searchUsersByName(name);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<?> checkEmailExists(@PathVariable String email) {
        try {
            boolean exists = userService.existsByEmail(email);
            return new ResponseEntity<>(exists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/exists/phone/{phoneNumber}")
    public ResponseEntity<?> checkPhoneExists(@PathVariable String phoneNumber) {
        try {
            boolean exists = userService.existsByPhoneNumber(phoneNumber);
            return new ResponseEntity<>(exists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/exists/aadhar/{aadharNumber}")
    public ResponseEntity<?> checkAadharExists(@PathVariable String aadharNumber) {
        try {
            boolean exists = userService.existsByAadharNumber(aadharNumber);
            return new ResponseEntity<>(exists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/{userId}/create-pin")
    public ResponseEntity<?> createTransferPin(@PathVariable Long userId, @RequestParam String pin) {
        try {
            User updatedUser = userService.createTransferPin(userId, pin);
            return new ResponseEntity<>("Transfer PIN created successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/{userId}/has-pin")
    public ResponseEntity<?> hasTransferPin(@PathVariable Long userId) {
        try {
            boolean hasPin = userService.hasTransferPin(userId);
            return new ResponseEntity<>(hasPin, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/{userId}/verify-pin")
    public ResponseEntity<?> verifyTransferPin(@PathVariable Long userId, @RequestParam String pin) {
        try {
            boolean isValid = userService.verifyTransferPin(userId, pin);
            return new ResponseEntity<>(isValid, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
