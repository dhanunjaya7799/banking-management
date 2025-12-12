package com.example.bankingsystem.service;

import com.example.bankingsystem.entity.Account;
import com.example.bankingsystem.entity.User;
import com.example.bankingsystem.repository.AccountRepository;
import com.example.bankingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Account createAccount(Account account) {
        validateAccountCreation(account);
        return accountRepository.save(account);
    }
    
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }
    
    public Optional<Account> getAccountByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }
    
    public List<Account> getAccountsByUser(User user) {
        return accountRepository.findByUser(user);
    }
    
    public List<Account> getAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }
    
    public List<Account> getAccountsByPhoneNumber(String phoneNumber) {
        return accountRepository.findByUserPhoneNumber(phoneNumber);
    }
    
    public List<Account> getActiveAccountsByPhoneNumber(String phoneNumber) {
        return accountRepository.findActiveAccountsByUserPhoneNumber(phoneNumber);
    }
    
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
    
    public List<Account> getAccountsByStatus(Account.AccountStatus status) {
        return accountRepository.findByStatus(status);
    }
    
    public List<Account> getAccountsByType(Account.AccountType accountType) {
        return accountRepository.findByAccountType(accountType);
    }
    
    public Account updateAccountStatus(Long accountId, Account.AccountStatus status) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            account.setStatus(status);
            return accountRepository.save(account);
        }
        throw new RuntimeException("Account not found with id: " + accountId);
    }
    
    public Account depositMoney(String accountNumber, BigDecimal amount) {
        Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (account.getStatus() != Account.AccountStatus.ACTIVE) {
                throw new RuntimeException("Account is not active");
            }
            
            BigDecimal newBalance = account.getBalance().add(amount);
            account.setBalance(newBalance);
            return accountRepository.save(account);
        }
        throw new RuntimeException("Account not found with account number: " + accountNumber);
    }
    
    public Account withdrawMoney(String accountNumber, BigDecimal amount) {
        Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (account.getStatus() != Account.AccountStatus.ACTIVE) {
                throw new RuntimeException("Account is not active");
            }
            
            if (account.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient balance");
            }
            
            BigDecimal newBalance = account.getBalance().subtract(amount);
            account.setBalance(newBalance);
            return accountRepository.save(account);
        }
        throw new RuntimeException("Account not found with account number: " + accountNumber);
    }
    
    public BigDecimal getAccountBalance(String accountNumber) {
        Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
        if (accountOpt.isPresent()) {
            return accountOpt.get().getBalance();
        }
        throw new RuntimeException("Account not found with account number: " + accountNumber);
    }
    
    public void deleteAccount(Long accountId) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                throw new RuntimeException("Cannot delete account with positive balance");
            }
            account.setStatus(Account.AccountStatus.CLOSED);
            accountRepository.save(account);
        } else {
            throw new RuntimeException("Account not found with id: " + accountId);
        }
    }
    
    public long getAccountCountByUser(User user) {
        return accountRepository.countAccountsByUser(user);
    }
    
    public List<Account> getActiveAccountsByUser(User user) {
        return accountRepository.findActiveAccountsByUser(user);
    }
    
    public boolean existsByAccountNumber(String accountNumber) {
        return accountRepository.existsByAccountNumber(accountNumber);
    }
    
    private void validateAccountCreation(Account account) {
        if (account.getUser() == null) {
            throw new RuntimeException("Account must be associated with a user");
        }
        
        if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Initial balance cannot be negative");
        }
        
        // Check if user exists
        if (!userRepository.existsById(account.getUser().getId())) {
            throw new RuntimeException("User does not exist");
        }
    }
}
