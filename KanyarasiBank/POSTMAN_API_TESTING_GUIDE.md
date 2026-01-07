# Postman API Testing Guide - Kanyarasi Bank

## Base URL
```
http://localhost:9990
```

---

## 1. AUTHENTICATION ENDPOINTS

### 1.1 Login (Public - No Auth Required)
**Method:** `POST`  
**URL:** `http://localhost:9990/api/auth/login`  
**Headers:**
```
Content-Type: application/json
```
**Body (raw JSON):**
```json
{
  "username": "admin",
  "password": "admin123"
}
```
**OR for User:**
```json
{
  "username": "your_username",
  "password": "your_password"
}
```

**Response:** Success message

---

### 1.2 Logout (Public - No Auth Required)
**Method:** `POST`  
**URL:** `http://localhost:9990/api/auth/logout`  
**Headers:**
```
Content-Type: application/json
```
**Body:** None

---

## 2. USER ENDPOINTS

### 2.1 Create Account Request (Public - No Auth Required)
**Method:** `POST`  
**URL:** `http://localhost:9990/api/user/create-account`  
**Headers:**
```
Content-Type: application/json
```
**Body (raw JSON):**
```json
{
  "user": {
    "fullName": "John Doe",
    "gender": "M",
    "dob": "1990-01-15",
    "nationality": "indian",
    "mobileNumber": 9876543210,
    "email": "john.doe@example.com",
    "address": "123 Main Street, City, State",
    "aadhar": 123456789012,
    "pan": "ABCDE1234F",
    "accountType": "savings",
    "initialDepositAmount": 5000
  },
  "username": "johndoe",
  "password": "password123",
  "transactionPin": "1234"
}
```

**Note:** 
- Account status will be set to "pending" and requires admin approval
- Initial deposit must be >= 1000
- Account types: "savings", "current", "salary", "student"

---

### 2.2 Make Transaction (Requires USER or ADMIN Auth)
**Method:** `POST`  
**URL:** `http://localhost:9990/api/user/transaction`  
**Headers:**
```
Content-Type: application/json
Authorization: Basic <base64(username:password)>
```
**Authentication:** Basic Auth with username and password

**Body for DEPOSIT (raw JSON):**
```json
{
  "transactionType": "DEPOSIT",
  "amount": 1000,
  "description": "Cash deposit",
  "transactionPin": "1234"
}
```

**Body for WITHDRAWAL (raw JSON):**
```json
{
  "transactionType": "WITHDRAWAL",
  "amount": 500,
  "description": "ATM withdrawal",
  "transactionPin": "1234"
}
```

**Body for TRANSFER (raw JSON):**
```json
{
  "transactionType": "TRANSFER",
  "amount": 2000,
  "description": "Transfer to friend",
  "transactionPin": "1234",
  "toAccountNumber": 987654321098
}
```

**Note:** 
- Transaction types: "DEPOSIT", "WITHDRAWAL", "TRANSFER"
- Account must be approved/active to make transactions
- Transaction PIN must match the account PIN

---

### 2.3 Get Transaction History (Requires USER or ADMIN Auth)
**Method:** `GET`  
**URL:** `http://localhost:9990/api/user/transaction-history`  
**Headers:**
```
Authorization: Basic <base64(username:password)>
```
**Authentication:** Basic Auth with username and password  
**Body:** None

**Response:** Array of transaction objects

---

## 3. ADMIN ENDPOINTS (All require ADMIN role)

### 3.1 Approve User Account
**Method:** `PUT`  
**URL:** `http://localhost:9990/api/admin/approve-account/{userId}`  
**Example:** `http://localhost:9990/api/admin/approve-account/1`  
**Headers:**
```
Authorization: Basic <base64(admin:admin123)>
```
**Authentication:** Basic Auth with admin credentials  
**Body:** None

---

### 3.2 Disapprove User Account
**Method:** `PUT`  
**URL:** `http://localhost:9990/api/admin/disapprove-account/{userId}`  
**Example:** `http://localhost:9990/api/admin/disapprove-account/1`  
**Headers:**
```
Authorization: Basic <base64(admin:admin123)>
```
**Authentication:** Basic Auth with admin credentials  
**Body:** None

---

### 3.3 Get All Users
**Method:** `GET`  
**URL:** `http://localhost:9990/api/admin/users`  
**Headers:**
```
Authorization: Basic <base64(admin:admin123)>
```
**Authentication:** Basic Auth with admin credentials  
**Body:** None

**Response:** Array of all user objects (excluding soft-deleted)

---

### 3.4 Get Pending Account Requests
**Method:** `GET`  
**URL:** `http://localhost:9990/api/admin/pending-requests`  
**Headers:**
```
Authorization: Basic <base64(admin:admin123)>
```
**Authentication:** Basic Auth with admin credentials  
**Body:** None

**Response:** Array of users with "pending" status

---

### 3.5 Get All Transactions
**Method:** `GET`  
**URL:** `http://localhost:9990/api/admin/transactions`  
**Headers:**
```
Authorization: Basic <base64(admin:admin123)>
```
**Authentication:** Basic Auth with admin credentials  
**Body:** None

**Response:** Array of all transaction objects

---

### 3.6 Delete Transaction
**Method:** `DELETE`  
**URL:** `http://localhost:9990/api/admin/transaction/{transactionId}`  
**Example:** `http://localhost:9990/api/admin/transaction/1`  
**Headers:**
```
Authorization: Basic <base64(admin:admin123)>
```
**Authentication:** Basic Auth with admin credentials  
**Body:** None

---

### 3.7 Soft Delete User Account
**Method:** `PUT`  
**URL:** `http://localhost:9990/api/admin/soft-delete-user/{userId}`  
**Example:** `http://localhost:9990/api/admin/soft-delete-user/1`  
**Headers:**
```
Authorization: Basic <base64(admin:admin123)>
```
**Authentication:** Basic Auth with admin credentials  
**Body:** None

---

## POSTMAN SETUP INSTRUCTIONS

### Setting up Basic Authentication in Postman:

1. **For each request that requires authentication:**
   - Go to the **Authorization** tab
   - Select **Basic Auth** from the Type dropdown
   - Enter:
     - **Username:** `admin` (for admin endpoints) or your user username
     - **Password:** `admin123` (for admin) or your user password

2. **Alternative method (Manual Header):**
   - Go to **Headers** tab
   - Add header:
     - **Key:** `Authorization`
     - **Value:** `Basic <base64_encoded_credentials>`
   - To encode: Use online base64 encoder with format `username:password`
   - Example: `admin:admin123` → `YWRtaW46YWRtaW4xMjM=`

### Testing Flow:

1. **First, create a user account:**
   - Use endpoint 2.1 (Create Account Request)
   - Note the userId from response or check database

2. **Login as admin:**
   - Use endpoint 1.1 with admin credentials

3. **Approve the user account:**
   - Use endpoint 3.1 with the userId from step 1

4. **Login as the user:**
   - Use endpoint 1.1 with user credentials

5. **Make transactions:**
   - Use endpoint 2.2 with user credentials

6. **View transaction history:**
   - Use endpoint 2.3 with user credentials

7. **Admin can view all data:**
   - Use endpoints 3.3, 3.4, 3.5 with admin credentials

---

## SAMPLE TESTING SCENARIO

### Step 1: Create User Account
```
POST http://localhost:9990/api/user/create-account
Body: {
  "user": {
    "fullName": "Jane Smith",
    "gender": "F",
    "dob": "1995-05-20",
    "nationality": "indian",
    "mobileNumber": 9876543211,
    "email": "jane.smith@example.com",
    "address": "456 Oak Avenue",
    "aadhar": 987654321098,
    "pan": "XYZAB5678G",
    "accountType": "savings",
    "initialDepositAmount": 10000
  },
  "username": "janesmith",
  "password": "jane123",
  "transactionPin": "5678"
}
```

### Step 2: Login as Admin
```
POST http://localhost:9990/api/auth/login
Body: {
  "username": "admin",
  "password": "admin123"
}
Auth: Basic (admin:admin123)
```

### Step 3: Approve User Account (assuming userId is 1)
```
PUT http://localhost:9990/api/admin/approve-account/1
Auth: Basic (admin:admin123)
```

### Step 4: Login as User
```
POST http://localhost:9990/api/auth/login
Body: {
  "username": "janesmith",
  "password": "jane123"
}
Auth: Basic (janesmith:jane123)
```

### Step 5: Make Deposit
```
POST http://localhost:9990/api/user/transaction
Body: {
  "transactionType": "DEPOSIT",
  "amount": 5000,
  "description": "Salary deposit",
  "transactionPin": "5678"
}
Auth: Basic (janesmith:jane123)
```

### Step 6: View Transaction History
```
GET http://localhost:9990/api/user/transaction-history
Auth: Basic (janesmith:jane123)
```

---

## NOTES

- All dates should be in format: `YYYY-MM-DD`
- Account numbers are 12-digit numbers (auto-generated)
- Transaction PIN is 4 digits (stored encrypted)
- Passwords are encrypted using BCrypt
- Account status values: "pending", "approved", "rejected", "active", "closed"
- Transaction status values: "SUCCESS", "FAILED", "PENDING"
- Transaction types: "DEPOSIT", "WITHDRAWAL", "TRANSFER"

