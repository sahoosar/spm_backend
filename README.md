# Stock Portfolio Management Service

This microservice manages user investment portfolios, holdings, and transactions.
Built with **Spring Boot, WebFlux, HikariCP, and MySQL**.

## Features
- User Portfolio Management
- Stock Holdings Tracking
- Transaction Logging (BUY/SELL)
- Global Exception Handling
- HikariCP Connection Pooling

## Tech Stack
- **Backend:** Java 17, Spring Boot 3.4.3, Spring WebFlux
- **Database:** MySQL 8.x with HikariCP
- **Build Tool:** Maven

## Database Schema
Schema Name: `spm_db`

###  Tables Overview
| Table Name           | Description                             |
|----------------------|-----------------------------------------|
| `users`              | Stores user details                     |
| `portfolios`         | Manages user investment accounts        |
| `holdings`           | Tracks stocks owned by users            |
| `trade_transactions` | Stores trade history (BUY/SELL)         |
| `stock_list`         | Lists all the stocks                    |
| `audit_logs`         | Tracks all modifications for compliance |



---
### Table Creation SQL Scripts
#### 1️⃣ **Users Table**
```sql
CREATE TABLE IF NOT EXISTS users (
    user_id VARCHAR(36) PRIMARY KEY,  
    user_name VARCHAR(60) UNIQUE NOT NULL,
    user_email VARCHAR(60) UNIQUE NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    password VARCHAR(300) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

 2️⃣ **Portfolios Table**
CREATE TABLE portfolios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_value DECIMAL(15,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

 3️⃣ **Holdings Table**
CREATE TABLE holdings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    portfolio_id BIGINT NOT NULL,
    stock_symbol VARCHAR(10) NOT NULL,
    quantity INT NOT NULL,
    purchase_price DECIMAL(10,2) NOT NULL,
    current_price DECIMAL(10,2) NOT NULL,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (portfolio_id) REFERENCES portfolios(id) ON DELETE CASCADE
);

 4️⃣ **Transactions Table**
CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    portfolio_id BIGINT NOT NULL,
    stock_symbol VARCHAR(10) NOT NULL,
    transaction_type ENUM('BUY', 'SELL') NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (portfolio_id) REFERENCES portfolios(id) ON DELETE CASCADE
);

 5️⃣ **Audit Logs Table**
CREATE TABLE audit_logs (
    log_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) ON DELETE SET NULL,
    action VARCHAR(255) NOT NULL,  -- e.g., 'BUY ORDER PLACED', 'SELL ORDER EXECUTED'
    details TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

---
### How to Set Up the Database
CREATE DATABASE spm_db;
USE spm_db;
SOURCE schema.sql;  -- Import tables from SQL file