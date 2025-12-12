package com.example.bankingsystem.controller;

import com.example.bankingsystem.entity.Transaction;
import com.example.bankingsystem.service.TransactionService;
import com.example.bankingsystem.service.PinService;
import com.example.bankingsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private PinService pinService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/transfer")
    public ResponseEntity<?> transferMoney(@RequestParam String fromAccountNumber,
                                         @RequestParam String toIdentifier,
                                         @RequestParam BigDecimal amount,
                                         @RequestParam(required = false) String description,
                                         @RequestParam(defaultValue = "false") boolean isPhoneNumber) {
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return new ResponseEntity<>("Amount must be positive", HttpStatus.BAD_REQUEST);
            }
            
            if (description == null || description.trim().isEmpty()) {
                description = "Money transfer";
            }
            
            Transaction transaction = transactionService.transferMoney(
                fromAccountNumber, toIdentifier, amount, description, isPhoneNumber);
            return new ResponseEntity<>(transaction, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/transfer/by-phone")
    public ResponseEntity<?> transferMoneyByPhone(@RequestParam String fromAccountNumber,
                                                @RequestParam String toPhoneNumber,
                                                @RequestParam BigDecimal amount,
                                                @RequestParam(required = false) String description,
                                                @RequestParam Long userId,
                                                @RequestParam String pin) {
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return new ResponseEntity<>("Amount must be positive", HttpStatus.BAD_REQUEST);
            }
            
            // Verify user's custom PIN
            if (!userService.verifyTransferPin(userId, pin)) {
                return new ResponseEntity<>("Invalid transfer PIN", HttpStatus.UNAUTHORIZED);
            }
            
            if (description == null || description.trim().isEmpty()) {
                description = "Money transfer via phone number";
            }
            
            Transaction transaction = transactionService.transferMoney(
                fromAccountNumber, toPhoneNumber, amount, description, true);
            return new ResponseEntity<>(transaction, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/transfer/by-account")
    public ResponseEntity<?> transferMoneyByAccount(@RequestParam String fromAccountNumber,
                                                  @RequestParam String toAccountNumber,
                                                  @RequestParam BigDecimal amount,
                                                  @RequestParam(required = false) String description,
                                                  @RequestParam Long userId,
                                                  @RequestParam String pin) {
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return new ResponseEntity<>("Amount must be positive", HttpStatus.BAD_REQUEST);
            }
            
            // Verify user's custom PIN
            if (!userService.verifyTransferPin(userId, pin)) {
                return new ResponseEntity<>("Invalid transfer PIN", HttpStatus.UNAUTHORIZED);
            }
            
            if (description == null || description.trim().isEmpty()) {
                description = "Money transfer via account number";
            }
            
            Transaction transaction = transactionService.transferMoney(
                fromAccountNumber, toAccountNumber, amount, description, false);
            return new ResponseEntity<>(transaction, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/deposit")
    public ResponseEntity<?> createDeposit(@RequestParam String accountNumber,
                                         @RequestParam BigDecimal amount,
                                         @RequestParam(required = false) String description) {
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return new ResponseEntity<>("Amount must be positive", HttpStatus.BAD_REQUEST);
            }
            
            if (description == null || description.trim().isEmpty()) {
                description = "Cash deposit";
            }
            
            Transaction transaction = transactionService.createDeposit(accountNumber, amount, description);
            return new ResponseEntity<>(transaction, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/withdraw")
    public ResponseEntity<?> createWithdrawal(@RequestParam String accountNumber,
                                            @RequestParam BigDecimal amount,
                                            @RequestParam(required = false) String description) {
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return new ResponseEntity<>("Amount must be positive", HttpStatus.BAD_REQUEST);
            }
            
            if (description == null || description.trim().isEmpty()) {
                description = "Cash withdrawal";
            }
            
            Transaction transaction = transactionService.createWithdrawal(accountNumber, amount, description);
            return new ResponseEntity<>(transaction, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long id) {
        try {
            Optional<Transaction> transaction = transactionService.getTransactionById(id);
            if (transaction.isPresent()) {
                return new ResponseEntity<>(transaction.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Transaction not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/transaction-id/{transactionId}")
    public ResponseEntity<?> getTransactionByTransactionId(@PathVariable String transactionId) {
        try {
            Optional<Transaction> transaction = transactionService.getTransactionByTransactionId(transactionId);
            if (transaction.isPresent()) {
                return new ResponseEntity<>(transaction.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Transaction not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/history/account/{accountNumber}")
    public ResponseEntity<?> getTransactionHistory(@PathVariable String accountNumber,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "20") int size) {
        try {
            if (size <= 0) {
                List<Transaction> transactions = transactionService.getTransactionHistory(accountNumber);
                return new ResponseEntity<>(transactions, HttpStatus.OK);
            } else {
                Pageable pageable = PageRequest.of(page, size);
                Page<Transaction> transactions = transactionService.getTransactionHistory(accountNumber, pageable);
                return new ResponseEntity<>(transactions, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/history/phone/{phoneNumber}")
    public ResponseEntity<?> getTransactionHistoryByPhoneNumber(@PathVariable String phoneNumber) {
        try {
            List<Transaction> transactions = transactionService.getTransactionHistoryByPhoneNumber(phoneNumber);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/history/account/{accountNumber}/date-range")
    public ResponseEntity<?> getTransactionHistoryByDateRange(
            @PathVariable String accountNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<Transaction> transactions = transactionService.getTransactionHistoryByDateRange(
                accountNumber, startDate, endDate);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllTransactions() {
        try {
            List<Transaction> transactions = transactionService.getAllTransactions();
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getTransactionsByStatus(@PathVariable String status) {
        try {
            Transaction.TransactionStatus transactionStatus = Transaction.TransactionStatus.valueOf(status.toUpperCase());
            List<Transaction> transactions = transactionService.getTransactionsByStatus(transactionStatus);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid status: " + status, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<?> getTransactionsByType(@PathVariable String type) {
        try {
            Transaction.TransactionType transactionType = Transaction.TransactionType.valueOf(type.toUpperCase());
            List<Transaction> transactions = transactionService.getTransactionsByType(transactionType);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid transaction type: " + type, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/count/account/{accountNumber}")
    public ResponseEntity<?> getTransactionCountByAccount(@PathVariable String accountNumber) {
        try {
            long count = transactionService.getTransactionCountByAccount(accountNumber);
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/completed/date-range")
    public ResponseEntity<?> getCompletedTransactionsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<Transaction> transactions = transactionService.getCompletedTransactionsBetweenDates(startDate, endDate);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
