package com.example.bankingsystem.controller;

import com.example.bankingsystem.dto.AccountRequestDto;
import com.example.bankingsystem.entity.AccountRequest;
import com.example.bankingsystem.entity.User;
import com.example.bankingsystem.service.AccountRequestService;
import com.example.bankingsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/account-requests")
@CrossOrigin(origins = "*")
public class AccountRequestController {
    
    @Autowired
    private AccountRequestService accountRequestService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    public ResponseEntity<?> createAccountRequest(@Valid @RequestBody AccountRequestDto requestDto) {
        try {
            // Get the user by ID
            Optional<User> userOpt = userService.getUserById(requestDto.getUserId());
            if (!userOpt.isPresent()) {
                return new ResponseEntity<>("User not found with id: " + requestDto.getUserId(), HttpStatus.NOT_FOUND);
            }
            
            // Create AccountRequest entity from DTO
            AccountRequest accountRequest = new AccountRequest();
            accountRequest.setUser(userOpt.get());
            accountRequest.setAccountType(requestDto.getAccountType());
            accountRequest.setInitialDeposit(requestDto.getInitialDeposit());
            
            AccountRequest createdRequest = accountRequestService.createAccountRequest(accountRequest);
            return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountRequestById(@PathVariable Long id) {
        try {
            Optional<AccountRequest> request = accountRequestService.getAccountRequestById(id);
            if (request.isPresent()) {
                return new ResponseEntity<>(request.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Account request not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/request-id/{requestId}")
    public ResponseEntity<?> getAccountRequestByRequestId(@PathVariable String requestId) {
        try {
            Optional<AccountRequest> request = accountRequestService.getAccountRequestByRequestId(requestId);
            if (request.isPresent()) {
                return new ResponseEntity<>(request.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Account request not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAccountRequestsByUserId(@PathVariable Long userId) {
        try {
            List<AccountRequest> requests = accountRequestService.getAccountRequestsByUserId(userId);
            return new ResponseEntity<>(requests, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllAccountRequests() {
        try {
            List<AccountRequest> requests = accountRequestService.getAllAccountRequests();
            return new ResponseEntity<>(requests, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getAccountRequestsByStatus(@PathVariable String status) {
        try {
            AccountRequest.RequestStatus requestStatus = AccountRequest.RequestStatus.valueOf(status.toUpperCase());
            List<AccountRequest> requests = accountRequestService.getAccountRequestsByStatus(requestStatus);
            return new ResponseEntity<>(requests, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid status: " + status, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingAccountRequests() {
        try {
            List<AccountRequest> requests = accountRequestService.getPendingAccountRequests();
            return new ResponseEntity<>(requests, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/{requestId}/approve")
    public ResponseEntity<?> approveAccountRequest(@PathVariable Long requestId,
                                                 @RequestParam Long staffId,
                                                 @RequestParam(required = false) String comments) {
        try {
            if (comments == null) {
                comments = "Account request approved";
            }
            
            AccountRequest approvedRequest = accountRequestService.approveAccountRequest(requestId, staffId, comments);
            return new ResponseEntity<>(approvedRequest, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<?> rejectAccountRequest(@PathVariable Long requestId,
                                                @RequestParam Long staffId,
                                                @RequestParam String rejectionReason,
                                                @RequestParam(required = false) String comments) {
        try {
            if (comments == null) {
                comments = "Account request rejected";
            }
            
            AccountRequest rejectedRequest = accountRequestService.rejectAccountRequest(
                requestId, staffId, rejectionReason, comments);
            return new ResponseEntity<>(rejectedRequest, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @PutMapping("/{requestId}/status/{status}")
    public ResponseEntity<?> updateRequestStatus(@PathVariable Long requestId, @PathVariable String status) {
        try {
            AccountRequest.RequestStatus requestStatus = AccountRequest.RequestStatus.valueOf(status.toUpperCase());
            AccountRequest updatedRequest = accountRequestService.updateRequestStatus(requestId, requestStatus);
            return new ResponseEntity<>(updatedRequest, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid status: " + status, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/status/{status}/date-range")
    public ResponseEntity<?> getRequestsByStatusAndDateRange(
            @PathVariable String status,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            AccountRequest.RequestStatus requestStatus = AccountRequest.RequestStatus.valueOf(status.toUpperCase());
            List<AccountRequest> requests = accountRequestService.getRequestsByStatusAndDateRange(
                requestStatus, startDate, endDate);
            return new ResponseEntity<>(requests, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid status: " + status, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/user/{userId}/pending-count")
    public ResponseEntity<?> getPendingRequestCountByUserId(@PathVariable Long userId) {
        try {
            Optional<User> userOpt = userService.getUserById(userId);
            if (userOpt.isPresent()) {
                long count = accountRequestService.getPendingRequestCountByUser(userOpt.get());
                return new ResponseEntity<>(count, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/pending-count")
    public ResponseEntity<?> getAllPendingRequestCount() {
        try {
            long count = accountRequestService.getAllPendingRequestCount();
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/staff/{staffId}/processed")
    public ResponseEntity<?> getRequestsProcessedByStaff(
            @PathVariable Long staffId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            Optional<User> staffOpt = userService.getUserById(staffId);
            if (staffOpt.isPresent()) {
                List<AccountRequest> requests = accountRequestService.getRequestsProcessedByStaff(
                    staffOpt.get(), startDate, endDate);
                return new ResponseEntity<>(requests, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Staff member not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/{requestId}")
    public ResponseEntity<?> deleteAccountRequest(@PathVariable Long requestId) {
        try {
            accountRequestService.deleteAccountRequest(requestId);
            return new ResponseEntity<>("Account request deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
