package com.example.bankingsystem.repository;

import com.example.bankingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    Optional<User> findByAadharNumber(String aadharNumber);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    boolean existsByAadharNumber(String aadharNumber);
    
    List<User> findByRole(User.UserRole role);
    
    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:name% OR u.lastName LIKE %:name%")
    List<User> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT u FROM User u WHERE u.role = 'STAFF' OR u.role = 'ADMIN'")
    List<User> findAllStaff();
}
