-- MySQL Database Setup Script for Banking System
-- Run this script to create the database and user

-- Create database
CREATE DATABASE IF NOT EXISTS bankingdb 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Use the database
USE bankingdb;

-- Create a dedicated user for the banking application (optional but recommended)
-- CREATE USER IF NOT EXISTS 'bankinguser'@'localhost' IDENTIFIED BY 'bankingpass123';
-- GRANT ALL PRIVILEGES ON bankingdb.* TO 'bankinguser'@'localhost';
-- FLUSH PRIVILEGES;

-- Note: The application will automatically create tables using JPA/Hibernate
-- The following tables will be created automatically:
-- 1. users - Store user information with Aadhar details
-- 2. accounts - Store bank account information
-- 3. transactions - Store all transaction records
-- 4. account_requests - Store account creation requests for staff approval

-- Verify database creation
SELECT 'Database bankingdb created successfully!' as Status;

-- Show database info
SELECT 
    SCHEMA_NAME as 'Database Name',
    DEFAULT_CHARACTER_SET_NAME as 'Character Set',
    DEFAULT_COLLATION_NAME as 'Collation'
FROM information_schema.SCHEMATA 
WHERE SCHEMA_NAME = 'bankingdb';
