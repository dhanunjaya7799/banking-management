package com.example.bankingsystem.repository;

import com.example.bankingsystem.entity.Account;
import com.example.bankingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    Optional<Account> findByAccountNumber(String accountNumber);
    
    List<Account> findByUser(User user);
    
    List<Account> findByUserId(Long userId);
    
    List<Account> findByStatus(Account.AccountStatus status);
    
    List<Account> findByAccountType(Account.AccountType accountType);
    
    @Query("SELECT a FROM Account a WHERE a.user.phoneNumber = :phoneNumber")
    List<Account> findByUserPhoneNumber(@Param("phoneNumber") String phoneNumber);
    
    @Query("SELECT a FROM Account a WHERE a.user.phoneNumber = :phoneNumber AND a.status = 'ACTIVE'")
    List<Account> findActiveAccountsByUserPhoneNumber(@Param("phoneNumber") String phoneNumber);
    
    boolean existsByAccountNumber(String accountNumber);
    
    @Query("SELECT COUNT(a) FROM Account a WHERE a.user = :user")
    long countAccountsByUser(@Param("user") User user);
    
    @Query("SELECT a FROM Account a WHERE a.user = :user AND a.status = 'ACTIVE'")
    List<Account> findActiveAccountsByUser(@Param("user") User user);
}
