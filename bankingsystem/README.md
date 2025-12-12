# Banking Management System

A comprehensive banking management system built with Spring Boot that provides CRUD operations for account management, money transfers, and staff approval workflows.

## Features

### Core Functionality
- **User Registration**: Create customer accounts with Aadhar verification
- **Account Management**: Create, update, and manage bank accounts
- **Money Transfers**: Transfer money between accounts using phone numbers or account numbers
- **Transaction History**: View complete transaction history with filtering options
- **Staff Approval**: Account creation requests require staff approval after Aadhar verification

### User Roles
- **Customer**: Can create account requests, transfer money, view transaction history
- **Staff**: Can approve/reject account requests, manage customer accounts
- **Admin**: Full system access and management capabilities

## Technology Stack

- **Backend**: Spring Boot 3.5.7
- **Database**: MySQL 8.0+ (Persistent database)
- **Security**: Spring Security
- **Validation**: Jakarta Validation
- **Build Tool**: Maven
- **Java Version**: 21

## Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd bankingsystem
```

2. Set up MySQL database:
```bash
# Create database
mysql -u root -p < mysql-setup.sql

# Or manually:
mysql -u root -p
CREATE DATABASE bankingdb;
```

3. Update database credentials:
```bash
# Edit src/main/resources/application.properties
# Update spring.datasource.password with your MySQL root password
```

4. Build the project:
```bash
mvn clean install
```

5. Run the application:
```bash
mvn spring-boot:run
```

6. Access the application:
- **Frontend**: `http://localhost:8080`
- **API Base URL**: `http://localhost:8080/api`
- **Database**: MySQL on localhost:3306/bankingdb

## API Endpoints

### User Management

#### Register User
```http
POST /api/users/register
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "phoneNumber": "9876543210",
  "aadharNumber": "123456789012",
  "dateOfBirth": "1990-01-01",
  "address": "123 Main Street, City"
}
```

#### Get User by ID
```http
GET /api/users/{id}
```

#### Get User by Phone Number
```http
GET /api/users/phone/{phoneNumber}
```

#### Update User (Phone/Email only)
```http
PUT /api/users/{id}
Content-Type: application/json

{
  "phoneNumber": "9876543211",
  "email": "newemail@example.com"
}
```

### Account Management

#### Get Accounts by Phone Number
```http
GET /api/accounts/phone/{phoneNumber}
```

#### Get Account Balance
```http
GET /api/accounts/{accountNumber}/balance
```

#### Deposit Money
```http
POST /api/accounts/{accountNumber}/deposit?amount=1000
```

#### Withdraw Money
```http
POST /api/accounts/{accountNumber}/withdraw?amount=500
```

### Money Transfers

#### Transfer by Phone Number
```http
POST /api/transactions/transfer/by-phone
Content-Type: application/x-www-form-urlencoded

fromAccountNumber=1234567890
toPhoneNumber=9876543210
amount=1000
description=Payment for services
```

#### Transfer by Account Number
```http
POST /api/transactions/transfer/by-account
Content-Type: application/x-www-form-urlencoded

fromAccountNumber=1234567890
toAccountNumber=0987654321
amount=1000
description=Money transfer
```

### Transaction History

#### Get Transaction History by Account
```http
GET /api/transactions/history/account/{accountNumber}
```

#### Get Transaction History by Phone Number
```http
GET /api/transactions/history/phone/{phoneNumber}
```

#### Get Transaction by ID
```http
GET /api/transactions/transaction-id/{transactionId}
```

### Account Requests (Staff Operations)

#### Create Account Request
```http
POST /api/account-requests
Content-Type: application/json

{
  "userId": 1,
  "accountType": "SAVINGS",
  "initialDeposit": 5000
}
```

#### Get Pending Requests
```http
GET /api/account-requests/pending
```

#### Approve Account Request
```http
POST /api/account-requests/{requestId}/approve?staffId=1&comments=Approved after verification
```

#### Reject Account Request
```http
POST /api/account-requests/{requestId}/reject?staffId=1&rejectionReason=Invalid documents&comments=Aadhar verification failed
```

## Sample Data

The application initializes with sample data:

### Users
- **Staff**: staff@bank.com (Phone: 9876543210)
- **Admin**: admin@bank.com (Phone: 9876543211)
- **Customer 1**: alice@example.com (Phone: 9876543212)
- **Customer 2**: bob@example.com (Phone: 9876543213)

### Accounts
- Alice Johnson: Savings Account with ₹10,000 balance
- Bob Smith: Current Account with ₹25,000 balance

## Business Logic

### Account Creation Workflow
1. Customer submits account request with personal details and Aadhar information
2. Request goes to staff for verification
3. Staff verifies Aadhar number and uploaded Aadhar image
4. Staff approves or rejects the request with comments
5. If approved, account is automatically created with initial deposit

### Money Transfer Rules
- Transfers can be made using phone number or account number
- Source account must have sufficient balance
- Both accounts must be active
- All transactions are logged with unique transaction IDs
- Failed transactions are recorded with failure reasons

### Update Restrictions
- Users can only update phone number and email
- Aadhar number cannot be changed once registered
- Account numbers are auto-generated and immutable

## Database Schema

### Key Entities
- **User**: Customer/Staff information with Aadhar details
- **Account**: Bank account with balance and status
- **Transaction**: All money movements with complete audit trail
- **AccountRequest**: Account creation requests for staff approval

### Relationships
- User → Account (One-to-Many)
- User → AccountRequest (One-to-Many)
- Account → Transaction (One-to-Many for both sender and receiver)
- AccountRequest → Account (One-to-One when approved)

## Security Features

- CORS enabled for cross-origin requests
- Input validation on all endpoints
- SQL injection prevention through JPA
- Error handling with detailed messages
- Transaction atomicity for money transfers

## Error Handling

The system provides comprehensive error handling:
- Validation errors with field-specific messages
- Business logic errors (insufficient funds, invalid accounts)
- System errors with appropriate HTTP status codes
- Detailed error responses for debugging

## Testing

Use tools like Postman or curl to test the API endpoints. The H2 console provides direct database access for verification.

## Future Enhancements

- JWT-based authentication
- File upload for Aadhar images
- Email notifications for transactions
- Account statements generation
- Mobile banking APIs
- Real-time transaction notifications

## Support

For issues or questions, please refer to the API documentation or contact the development team.
