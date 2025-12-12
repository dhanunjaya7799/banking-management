package com.example.bankingsystem.repository;

import com.example.bankingsystem.entity.Account;
import com.example.bankingsystem.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Optional<Transaction> findByTransactionId(String transactionId);
    
    List<Transaction> findByFromAccount(Account fromAccount);
    
    List<Transaction> findByToAccount(Account toAccount);
    
    @Query("SELECT t FROM Transaction t WHERE t.fromAccount = :account OR t.toAccount = :account ORDER BY t.transactionDate DESC")
    List<Transaction> findAllTransactionsByAccount(@Param("account") Account account);
    
    @Query("SELECT t FROM Transaction t WHERE t.fromAccount = :account OR t.toAccount = :account ORDER BY t.transactionDate DESC")
    Page<Transaction> findAllTransactionsByAccount(@Param("account") Account account, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE (t.fromAccount = :account OR t.toAccount = :account) " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC")
    List<Transaction> findTransactionsByAccountAndDateRange(
        @Param("account") Account account, 
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT t FROM Transaction t WHERE t.fromAccount.user.phoneNumber = :phoneNumber " +
           "OR t.toAccount.user.phoneNumber = :phoneNumber ORDER BY t.transactionDate DESC")
    List<Transaction> findTransactionsByUserPhoneNumber(@Param("phoneNumber") String phoneNumber);
    
    List<Transaction> findByStatus(Transaction.TransactionStatus status);
    
    List<Transaction> findByTransactionType(Transaction.TransactionType transactionType);
    
    @Query("SELECT t FROM Transaction t WHERE t.status = 'COMPLETED' " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findCompletedTransactionsBetweenDates(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.fromAccount = :account OR t.toAccount = :account")
    long countTransactionsByAccount(@Param("account") Account account);
}
