# Tiny Ledger

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)

Tiny Ledger is a lightweight Spring Boot application for managing basic ledger entries. It supports CRUD operations and
is designed for simplicity and ease of use.

---

## Features

- **CRUD Operations**: Create, Read, Update ledger entries.
- **REST API**: Exposes endpoints for managing ledger entries.
- **SQLite Database**: Lightweight, file-based database for easy setup.
- [Swagger UI](swagger.html): Interactive API documentation for testing endpoints.
  - Open this file in any browser to access the Swagger UI properly.
- [Postman Collection](teya.postman_collection.json): Import this collection into **Postman** to test API requests.
---

## Prerequisites

- **JDK 17 or higher**
- **Maven 3.x**
- **Postman** (optional, for API testing)

---

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/tiny-ledger.git
cd tiny-ledger
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application will start at http://localhost:8080

## Endpoints

### 1. Create a Transaction Entry

- **Method**: `POST`
- **URL**: `/api/teya/transactions/deposit/account/{accountId}`
- **Request Body**:
  ```json
  {
    "amount": 100,
  }
- **Response**:

  ```json
  {
    "message": "Transaction completed successfully."
  }
    ```

- **Method**: `POST`
- **URL**: `/api/teya/transactions/withdraw/account/{accountId}`
- **Request Body**:
  ```json
  {
    "amount": 100.0,
  }
- **Response**:

  ```json
  {
    "message": "Transaction completed successfully."
  }

### 2. Get Transaction History

- **Method**: `GET`
- **URL**: `/api/teya/transactions/history/account/{accountId}?lastTransactionId=123&limit=10`
  
- lastTransactionId is Optional

- **Response**:
  ```json 
  {
    "transactions": [
       {
        "id": 7,
        "type": "WITHDRAWAL",
        "amount": 10,
        "timestamp": "2025-03-01T22:44:58",
        "status": "COMPLETED"
       },
       {
        "id": 6,
        "type": "DEPOSIT",
        "amount": 1000,
        "timestamp": "2025-03-01T22:44:49",
        "status": "COMPLETED"
       }
    ],
    "metadata": {
        "records": 2,
        "lastSeenId": 6
    }
  }
### 3. Get Account Balance

- **Method**: `GET`
- **URL**: `/api/teya/balance/account/{accountId}`
    
- **Response**:

    ```
  1000 
    ```
     
