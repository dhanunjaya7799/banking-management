package com.example.bankingsystem.controller;

import com.example.bankingsystem.entity.Account;
import com.example.bankingsystem.entity.User;
import com.example.bankingsystem.service.AccountService;
import com.example.bankingsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountController {
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    public ResponseEntity<?> createAccount(@Valid @RequestBody Account account) {
        try {
            Account createdAccount = accountService.createAccount(account);
            return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable Long id) {
        try {
            Optional<Account> account = accountService.getAccountById(id);
            if (account.isPresent()) {
                return new ResponseEntity<>(account.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Account not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<?> getAccountByAccountNumber(@PathVariable String accountNumber) {
        try {
            Optional<Account> account = accountService.getAccountByAccountNumber(accountNumber);
            if (account.isPresent()) {
                return new ResponseEntity<>(account.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Account not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAccountsByUserId(@PathVariable Long userId) {
        try {
            List<Account> accounts = accountService.getAccountsByUserId(userId);
            return new ResponseEntity<>(accounts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<?> getAccountsByPhoneNumber(@PathVariable String phoneNumber) {
        try {
            List<Account> accounts = accountService.getAccountsByPhoneNumber(phoneNumber);
            return new ResponseEntity<>(accounts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/phone/{phoneNumber}/active")
    public ResponseEntity<?> getActiveAccountsByPhoneNumber(@PathVariable String phoneNumber) {
        try {
            List<Account> accounts = accountService.getActiveAccountsByPhoneNumber(phoneNumber);
            return new ResponseEntity<>(accounts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllAccounts() {
        try {
            List<Account> accounts = accountService.getAllAccounts();
            return new ResponseEntity<>(accounts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getAccountsByStatus(@PathVariable String status) {
        try {
            Account.AccountStatus accountStatus = Account.AccountStatus.valueOf(status.toUpperCase());
            List<Account> accounts = accountService.getAccountsByStatus(accountStatus);
            return new ResponseEntity<>(accounts, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid status: " + status, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<?> getAccountsByType(@PathVariable String type) {
        try {
            Account.AccountType accountType = Account.AccountType.valueOf(type.toUpperCase());
            List<Account> accounts = accountService.getAccountsByType(accountType);
            return new ResponseEntity<>(accounts, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid account type: " + type, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping("/{accountId}/status/{status}")
    public ResponseEntity<?> updateAccountStatus(@PathVariable Long accountId, @PathVariable String status) {
        try {
            Account.AccountStatus accountStatus = Account.AccountStatus.valueOf(status.toUpperCase());
            Account updatedAccount = accountService.updateAccountStatus(accountId, accountStatus);
            return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid status: " + status, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<?> depositMoney(@PathVariable String accountNumber, 
                                        @RequestParam BigDecimal amount) {
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return new ResponseEntity<>("Amount must be positive", HttpStatus.BAD_REQUEST);
            }
            Account updatedAccount = accountService.depositMoney(accountNumber, amount);
            return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<?> withdrawMoney(@PathVariable String accountNumber, 
                                         @RequestParam BigDecimal amount) {
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return new ResponseEntity<>("Amount must be positive", HttpStatus.BAD_REQUEST);
            }
            Account updatedAccount = accountService.withdrawMoney(accountNumber, amount);
            return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<?> getAccountBalance(@PathVariable String accountNumber) {
        try {
            BigDecimal balance = accountService.getAccountBalance(accountNumber);
            return new ResponseEntity<>(balance, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @DeleteMapping("/{accountId}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long accountId) {
        try {
            accountService.deleteAccount(accountId);
            return new ResponseEntity<>("Account closed successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<?> getAccountCountByUserId(@PathVariable Long userId) {
        try {
            Optional<User> userOpt = userService.getUserById(userId);
            if (userOpt.isPresent()) {
                long count = accountService.getAccountCountByUser(userOpt.get());
                return new ResponseEntity<>(count, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<?> getActiveAccountsByUserId(@PathVariable Long userId) {
        try {
            Optional<User> userOpt = userService.getUserById(userId);
            if (userOpt.isPresent()) {
                List<Account> accounts = accountService.getActiveAccountsByUser(userOpt.get());
                return new ResponseEntity<>(accounts, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/exists/{accountNumber}")
    public ResponseEntity<?> checkAccountExists(@PathVariable String accountNumber) {
        try {
            boolean exists = accountService.existsByAccountNumber(accountNumber);
            return new ResponseEntity<>(exists, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
