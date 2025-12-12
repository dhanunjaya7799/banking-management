package com.example.bankingsystem.service;

import com.example.bankingsystem.entity.Account;
import com.example.bankingsystem.entity.Transaction;
import com.example.bankingsystem.repository.AccountRepository;
import com.example.bankingsystem.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private AccountService accountService;
    
    public Transaction transferMoney(String fromAccountNumber, String toIdentifier, 
                                   BigDecimal amount, String description, boolean isPhoneNumber) {
        
        // Get source account
        Optional<Account> fromAccountOpt = accountRepository.findByAccountNumber(fromAccountNumber);
        if (!fromAccountOpt.isPresent()) {
            throw new RuntimeException("Source account not found: " + fromAccountNumber);
        }
        
        Account fromAccount = fromAccountOpt.get();
        if (fromAccount.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new RuntimeException("Source account is not active");
        }
        
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance in source account");
        }
        
        // Get destination account
        Account toAccount = null;
        if (isPhoneNumber) {
            List<Account> accounts = accountRepository.findActiveAccountsByUserPhoneNumber(toIdentifier);
            if (accounts.isEmpty()) {
                throw new RuntimeException("No active account found for phone number: " + toIdentifier);
            }
            // Use the first active account if multiple accounts exist
            toAccount = accounts.get(0);
        } else {
            Optional<Account> toAccountOpt = accountRepository.findByAccountNumber(toIdentifier);
            if (!toAccountOpt.isPresent()) {
                throw new RuntimeException("Destination account not found: " + toIdentifier);
            }
            toAccount = toAccountOpt.get();
        }
        
        if (toAccount.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new RuntimeException("Destination account is not active");
        }
        
        if (fromAccount.getId().equals(toAccount.getId())) {
            throw new RuntimeException("Cannot transfer money to the same account");
        }
        
        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setTransactionType(Transaction.TransactionType.TRANSFER);
        transaction.setAmount(amount);
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setDescription(description);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        
        if (isPhoneNumber) {
            transaction.setRecipientPhone(toIdentifier);
        } else {
            transaction.setRecipientAccountNumber(toIdentifier);
        }
        
        try {
            // Perform the transfer
            accountService.withdrawMoney(fromAccountNumber, amount);
            accountService.depositMoney(toAccount.getAccountNumber(), amount);
            
            // Mark transaction as completed
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            
        } catch (Exception e) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transaction.setDescription(transaction.getDescription() + " - Failed: " + e.getMessage());
        }
        
        return transactionRepository.save(transaction);
    }
    
    public Transaction createDeposit(String accountNumber, BigDecimal amount, String description) {
        Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
        if (!accountOpt.isPresent()) {
            throw new RuntimeException("Account not found: " + accountNumber);
        }
        
        Account account = accountOpt.get();
        
        Transaction transaction = new Transaction();
        transaction.setTransactionType(Transaction.TransactionType.DEPOSIT);
        transaction.setAmount(amount);
        transaction.setToAccount(account);
        transaction.setDescription(description);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        
        try {
            accountService.depositMoney(accountNumber, amount);
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        } catch (Exception e) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transaction.setDescription(transaction.getDescription() + " - Failed: " + e.getMessage());
        }
        
        return transactionRepository.save(transaction);
    }
    
    public Transaction createWithdrawal(String accountNumber, BigDecimal amount, String description) {
        Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
        if (!accountOpt.isPresent()) {
            throw new RuntimeException("Account not found: " + accountNumber);
        }
        
        Account account = accountOpt.get();
        
        Transaction transaction = new Transaction();
        transaction.setTransactionType(Transaction.TransactionType.WITHDRAWAL);
        transaction.setAmount(amount);
        transaction.setFromAccount(account);
        transaction.setDescription(description);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        
        try {
            accountService.withdrawMoney(accountNumber, amount);
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        } catch (Exception e) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transaction.setDescription(transaction.getDescription() + " - Failed: " + e.getMessage());
        }
        
        return transactionRepository.save(transaction);
    }
    
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }
    
    public Optional<Transaction> getTransactionByTransactionId(String transactionId) {
        return transactionRepository.findByTransactionId(transactionId);
    }
    
    public List<Transaction> getTransactionHistory(String accountNumber) {
        Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
        if (accountOpt.isPresent()) {
            return transactionRepository.findAllTransactionsByAccount(accountOpt.get());
        }
        throw new RuntimeException("Account not found: " + accountNumber);
    }
    
    public Page<Transaction> getTransactionHistory(String accountNumber, Pageable pageable) {
        Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
        if (accountOpt.isPresent()) {
            return transactionRepository.findAllTransactionsByAccount(accountOpt.get(), pageable);
        }
        throw new RuntimeException("Account not found: " + accountNumber);
    }
    
    public List<Transaction> getTransactionHistoryByPhoneNumber(String phoneNumber) {
        return transactionRepository.findTransactionsByUserPhoneNumber(phoneNumber);
    }
    
    public List<Transaction> getTransactionHistoryByDateRange(String accountNumber, 
                                                            LocalDateTime startDate, 
                                                            LocalDateTime endDate) {
        Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
        if (accountOpt.isPresent()) {
            return transactionRepository.findTransactionsByAccountAndDateRange(
                accountOpt.get(), startDate, endDate);
        }
        throw new RuntimeException("Account not found: " + accountNumber);
    }
    
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    public List<Transaction> getTransactionsByStatus(Transaction.TransactionStatus status) {
        return transactionRepository.findByStatus(status);
    }
    
    public List<Transaction> getTransactionsByType(Transaction.TransactionType type) {
        return transactionRepository.findByTransactionType(type);
    }
    
    public long getTransactionCountByAccount(String accountNumber) {
        Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
        if (accountOpt.isPresent()) {
            return transactionRepository.countTransactionsByAccount(accountOpt.get());
        }
        throw new RuntimeException("Account not found: " + accountNumber);
    }
    
    public List<Transaction> getCompletedTransactionsBetweenDates(LocalDateTime startDate, 
                                                                LocalDateTime endDate) {
        return transactionRepository.findCompletedTransactionsBetweenDates(startDate, endDate);
    }
}
