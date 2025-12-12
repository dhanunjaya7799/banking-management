package com.example.bankingsystem.config;

import com.example.bankingsystem.entity.Account;
import com.example.bankingsystem.entity.User;
import com.example.bankingsystem.service.AccountService;
import com.example.bankingsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Override
    public void run(String... args) throws Exception {
        initializeData();
    }

    private void initializeData() {
        try {
            // Create sample staff user
            User staff = new User();
            staff.setFirstName("John");
            staff.setLastName("Staff");
            staff.setEmail("staff@bank.com");
            staff.setPhoneNumber("9876543210");
            staff.setAadharNumber("123456789012");
            staff.setDateOfBirth("1985-01-15");
            staff.setAddress("123 Bank Street, City");
            staff.setRole(User.UserRole.STAFF);
            userService.createUser(staff);

            // Create sample admin user
            User admin = new User();
            admin.setFirstName("Jane");
            admin.setLastName("Admin");
            admin.setEmail("admin@bank.com");
            admin.setPhoneNumber("9876543211");
            admin.setAadharNumber("123456789013");
            admin.setDateOfBirth("1980-05-20");
            admin.setAddress("456 Admin Avenue, City");
            admin.setRole(User.UserRole.ADMIN);
            userService.createUser(admin);

            // Create sample customer users
            User customer1 = new User();
            customer1.setFirstName("Alice");
            customer1.setLastName("Johnson");
            customer1.setEmail("alice@example.com");
            customer1.setPhoneNumber("9876543212");
            customer1.setAadharNumber("123456789014");
            customer1.setDateOfBirth("1990-03-10");
            customer1.setAddress("789 Customer Lane, City");
            customer1.setRole(User.UserRole.CUSTOMER);
            User savedCustomer1 = userService.createUser(customer1);

            User customer2 = new User();
            customer2.setFirstName("Bob");
            customer2.setLastName("Smith");
            customer2.setEmail("bob@example.com");
            customer2.setPhoneNumber("9876543213");
            customer2.setAadharNumber("123456789015");
            customer2.setDateOfBirth("1988-07-25");
            customer2.setAddress("321 Smith Street, City");
            customer2.setRole(User.UserRole.CUSTOMER);
            User savedCustomer2 = userService.createUser(customer2);

            // Create sample accounts
            Account account1 = new Account();
            account1.setAccountType(Account.AccountType.SAVINGS);
            account1.setBalance(new BigDecimal("10000.00"));
            account1.setUser(savedCustomer1);
            account1.setStatus(Account.AccountStatus.ACTIVE);
            accountService.createAccount(account1);

            Account account2 = new Account();
            account2.setAccountType(Account.AccountType.CURRENT);
            account2.setBalance(new BigDecimal("25000.00"));
            account2.setUser(savedCustomer2);
            account2.setStatus(Account.AccountStatus.ACTIVE);
            accountService.createAccount(account2);

            System.out.println("Sample data initialized successfully!");
            System.out.println("Staff Login: staff@bank.com");
            System.out.println("Admin Login: admin@bank.com");
            System.out.println("Customer 1: alice@example.com (Phone: 9876543212)");
            System.out.println("Customer 2: bob@example.com (Phone: 9876543213)");

        } catch (Exception e) {
            System.err.println("Error initializing sample data: " + e.getMessage());
        }
    }
}
