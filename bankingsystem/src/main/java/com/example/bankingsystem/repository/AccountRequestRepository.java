package com.example.bankingsystem.repository;

import com.example.bankingsystem.entity.AccountRequest;
import com.example.bankingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRequestRepository extends JpaRepository<AccountRequest, Long> {
    
    Optional<AccountRequest> findByRequestId(String requestId);
    
    List<AccountRequest> findByUser(User user);
    
    List<AccountRequest> findByUserId(Long userId);
    
    List<AccountRequest> findByStatus(AccountRequest.RequestStatus status);
    
    List<AccountRequest> findByProcessedByStaff(User staff);
    
    @Query("SELECT ar FROM AccountRequest ar WHERE ar.status = 'PENDING' ORDER BY ar.createdAt ASC")
    List<AccountRequest> findPendingRequestsOrderByCreatedDate();
    
    @Query("SELECT ar FROM AccountRequest ar WHERE ar.status = :status " +
           "AND ar.createdAt BETWEEN :startDate AND :endDate ORDER BY ar.createdAt DESC")
    List<AccountRequest> findRequestsByStatusAndDateRange(
        @Param("status") AccountRequest.RequestStatus status,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT COUNT(ar) FROM AccountRequest ar WHERE ar.user = :user AND ar.status = 'PENDING'")
    long countPendingRequestsByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(ar) FROM AccountRequest ar WHERE ar.status = 'PENDING'")
    long countAllPendingRequests();
    
    @Query("SELECT ar FROM AccountRequest ar WHERE ar.processedByStaff = :staff " +
           "AND ar.processedAt BETWEEN :startDate AND :endDate")
    List<AccountRequest> findRequestsProcessedByStaffInDateRange(
        @Param("staff") User staff,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
