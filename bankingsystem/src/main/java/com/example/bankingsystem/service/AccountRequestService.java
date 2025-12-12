package com.example.bankingsystem.service;

import com.example.bankingsystem.entity.Account;
import com.example.bankingsystem.entity.AccountRequest;
import com.example.bankingsystem.entity.User;
import com.example.bankingsystem.repository.AccountRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AccountRequestService {
    
    @Autowired
    private AccountRequestRepository accountRequestRepository;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private UserService userService;
    
    public AccountRequest createAccountRequest(AccountRequest accountRequest) {
        validateAccountRequest(accountRequest);
        return accountRequestRepository.save(accountRequest);
    }
    
    public Optional<AccountRequest> getAccountRequestById(Long id) {
        return accountRequestRepository.findById(id);
    }
    
    public Optional<AccountRequest> getAccountRequestByRequestId(String requestId) {
        return accountRequestRepository.findByRequestId(requestId);
    }
    
    public List<AccountRequest> getAccountRequestsByUser(User user) {
        return accountRequestRepository.findByUser(user);
    }
    
    public List<AccountRequest> getAccountRequestsByUserId(Long userId) {
        return accountRequestRepository.findByUserId(userId);
    }
    
    public List<AccountRequest> getAllAccountRequests() {
        return accountRequestRepository.findAll();
    }
    
    public List<AccountRequest> getAccountRequestsByStatus(AccountRequest.RequestStatus status) {
        return accountRequestRepository.findByStatus(status);
    }
    
    public List<AccountRequest> getPendingAccountRequests() {
        return accountRequestRepository.findPendingRequestsOrderByCreatedDate();
    }
    
    public AccountRequest approveAccountRequest(Long requestId, Long staffId, String comments) {
        Optional<AccountRequest> requestOpt = accountRequestRepository.findById(requestId);
        if (!requestOpt.isPresent()) {
            throw new RuntimeException("Account request not found with id: " + requestId);
        }
        
        AccountRequest request = requestOpt.get();
        if (request.getStatus() != AccountRequest.RequestStatus.PENDING) {
            throw new RuntimeException("Only pending requests can be approved");
        }
        
        Optional<User> staffOpt = userService.getUserById(staffId);
        if (!staffOpt.isPresent()) {
            throw new RuntimeException("Staff member not found with id: " + staffId);
        }
        
        User staff = staffOpt.get();
        if (staff.getRole() != User.UserRole.STAFF && staff.getRole() != User.UserRole.ADMIN) {
            throw new RuntimeException("Only staff members can approve requests");
        }
        
        try {
            // Create the account
            Account newAccount = new Account();
            newAccount.setAccountType(request.getAccountType());
            newAccount.setBalance(request.getInitialDeposit());
            newAccount.setUser(request.getUser());
            newAccount.setStatus(Account.AccountStatus.ACTIVE);
            
            Account createdAccount = accountService.createAccount(newAccount);
            
            // Update the request
            request.setStatus(AccountRequest.RequestStatus.APPROVED);
            request.setProcessedByStaff(staff);
            request.setProcessedAt(LocalDateTime.now());
            request.setStaffComments(comments);
            request.setCreatedAccount(createdAccount);
            
            return accountRequestRepository.save(request);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to approve account request: " + e.getMessage());
        }
    }
    
    public AccountRequest rejectAccountRequest(Long requestId, Long staffId, 
                                             String rejectionReason, String comments) {
        Optional<AccountRequest> requestOpt = accountRequestRepository.findById(requestId);
        if (!requestOpt.isPresent()) {
            throw new RuntimeException("Account request not found with id: " + requestId);
        }
        
        AccountRequest request = requestOpt.get();
        if (request.getStatus() != AccountRequest.RequestStatus.PENDING) {
            throw new RuntimeException("Only pending requests can be rejected");
        }
        
        Optional<User> staffOpt = userService.getUserById(staffId);
        if (!staffOpt.isPresent()) {
            throw new RuntimeException("Staff member not found with id: " + staffId);
        }
        
        User staff = staffOpt.get();
        if (staff.getRole() != User.UserRole.STAFF && staff.getRole() != User.UserRole.ADMIN) {
            throw new RuntimeException("Only staff members can reject requests");
        }
        
        request.setStatus(AccountRequest.RequestStatus.REJECTED);
        request.setProcessedByStaff(staff);
        request.setProcessedAt(LocalDateTime.now());
        request.setRejectionReason(rejectionReason);
        request.setStaffComments(comments);
        
        return accountRequestRepository.save(request);
    }
    
    public AccountRequest updateRequestStatus(Long requestId, AccountRequest.RequestStatus status) {
        Optional<AccountRequest> requestOpt = accountRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            AccountRequest request = requestOpt.get();
            request.setStatus(status);
            return accountRequestRepository.save(request);
        }
        throw new RuntimeException("Account request not found with id: " + requestId);
    }
    
    public List<AccountRequest> getRequestsByStatusAndDateRange(AccountRequest.RequestStatus status,
                                                              LocalDateTime startDate,
                                                              LocalDateTime endDate) {
        return accountRequestRepository.findRequestsByStatusAndDateRange(status, startDate, endDate);
    }
    
    public long getPendingRequestCountByUser(User user) {
        return accountRequestRepository.countPendingRequestsByUser(user);
    }
    
    public long getAllPendingRequestCount() {
        return accountRequestRepository.countAllPendingRequests();
    }
    
    public List<AccountRequest> getRequestsProcessedByStaff(User staff, 
                                                          LocalDateTime startDate,
                                                          LocalDateTime endDate) {
        return accountRequestRepository.findRequestsProcessedByStaffInDateRange(staff, startDate, endDate);
    }
    
    public void deleteAccountRequest(Long requestId) {
        Optional<AccountRequest> requestOpt = accountRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            AccountRequest request = requestOpt.get();
            if (request.getStatus() == AccountRequest.RequestStatus.PENDING) {
                throw new RuntimeException("Cannot delete pending requests");
            }
            accountRequestRepository.deleteById(requestId);
        } else {
            throw new RuntimeException("Account request not found with id: " + requestId);
        }
    }
    
    private void validateAccountRequest(AccountRequest accountRequest) {
        if (accountRequest.getUser() == null) {
            throw new RuntimeException("Account request must be associated with a user");
        }
        
        if (accountRequest.getAccountType() == null) {
            throw new RuntimeException("Account type is required");
        }
        
        if (accountRequest.getInitialDeposit().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Initial deposit cannot be negative");
        }
        
        // Check if user has too many pending requests
        long pendingCount = accountRequestRepository.countPendingRequestsByUser(accountRequest.getUser());
        if (pendingCount >= 3) {
            throw new RuntimeException("User has too many pending account requests");
        }
    }
}
