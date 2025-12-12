# MySQL Setup Guide for Banking System

This guide will help you set up MySQL database for the Banking Management System.

## Prerequisites

1. **MySQL Server**: Install MySQL Server 8.0 or later
   - Download from: https://dev.mysql.com/downloads/mysql/
   - Or use package manager:
     ```bash
     # Windows (using Chocolatey)
     choco install mysql
     
     # macOS (using Homebrew)
     brew install mysql
     
     # Ubuntu/Debian
     sudo apt update
     sudo apt install mysql-server
     ```

2. **MySQL Client**: Ensure you have MySQL command line client or MySQL Workbench

## Setup Steps

### Step 1: Start MySQL Service

```bash
# Windows
net start mysql

# macOS/Linux
sudo systemctl start mysql
# or
sudo service mysql start
```

### Step 2: Connect to MySQL

```bash
mysql -u root -p
```

### Step 3: Create Database and User

Run the provided SQL script:

```bash
mysql -u root -p < mysql-setup.sql
```

Or execute manually:

```sql
-- Create database
CREATE DATABASE IF NOT EXISTS bankingdb 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Use the database
USE bankingdb;

-- Optional: Create dedicated user (recommended for production)
CREATE USER IF NOT EXISTS 'bankinguser'@'localhost' IDENTIFIED BY 'bankingpass123';
GRANT ALL PRIVILEGES ON bankingdb.* TO 'bankinguser'@'localhost';
FLUSH PRIVILEGES;
```

### Step 4: Update Application Configuration

The application.properties has been updated with MySQL configuration:

```properties
# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/bankingdb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=password

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
```

**Important**: Update the password in `application.properties` to match your MySQL root password.

### Step 5: Alternative Configuration (Using Dedicated User)

If you created a dedicated user, update application.properties:

```properties
spring.datasource.username=bankinguser
spring.datasource.password=bankingpass123
```

## Configuration Options

### Database URL Parameters Explained

- `createDatabaseIfNotExist=true`: Automatically creates database if it doesn't exist
- `useSSL=false`: Disables SSL for local development (enable in production)
- `allowPublicKeyRetrieval=true`: Allows public key retrieval for authentication
- `serverTimezone=UTC`: Sets timezone to UTC

### Hibernate DDL Options

- `create-drop`: Creates tables on startup, drops on shutdown (development only)
- `update`: Updates schema without dropping data (recommended)
- `validate`: Only validates schema
- `none`: No automatic schema management

## Troubleshooting

### Common Issues

1. **Connection Refused**
   ```
   Solution: Ensure MySQL service is running
   Windows: net start mysql
   Linux/macOS: sudo systemctl start mysql
   ```

2. **Access Denied**
   ```
   Solution: Check username/password in application.properties
   Verify user has proper permissions on the database
   ```

3. **Database Does Not Exist**
   ```
   Solution: The URL parameter createDatabaseIfNotExist=true should handle this
   Or manually create database using the setup script
   ```

4. **Timezone Issues**
   ```
   Solution: Add serverTimezone=UTC to the database URL
   Or set MySQL timezone: SET GLOBAL time_zone = '+00:00';
   ```

### Verification Commands

Check if database was created:
```sql
SHOW DATABASES;
USE bankingdb;
SHOW TABLES;
```

Check user permissions:
```sql
SHOW GRANTS FOR 'root'@'localhost';
-- or
SHOW GRANTS FOR 'bankinguser'@'localhost';
```

## Production Considerations

1. **Security**:
   - Use dedicated database user with minimal privileges
   - Enable SSL connections
   - Use strong passwords
   - Restrict network access

2. **Performance**:
   - Configure appropriate connection pool settings
   - Set up proper indexes
   - Monitor query performance

3. **Backup**:
   - Set up regular database backups
   - Test backup restoration procedures

## Sample Production Configuration

```properties
# Production MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/bankingdb?useSSL=true&requireSSL=true&serverTimezone=UTC
spring.datasource.username=bankinguser
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Connection Pool Settings
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
```

## Testing the Setup

1. Start the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```

2. Check application logs for successful database connection

3. Verify tables are created:
   ```sql
   USE bankingdb;
   SHOW TABLES;
   DESCRIBE users;
   DESCRIBE accounts;
   DESCRIBE transactions;
   DESCRIBE account_requests;
   ```

4. Test the application functionality through the web interface

## Migration from H2

The application has been updated to use MySQL instead of H2. Key changes:

1. **Dependencies**: Replaced H2 with MySQL connector
2. **Configuration**: Updated database URL and dialect
3. **Entity Optimizations**: Added MySQL-specific column definitions
4. **Schema Management**: Changed from `create-drop` to `update` for data persistence

Your existing data from H2 will not be migrated automatically. If you need to preserve data, export it from H2 and import into MySQL manually.
