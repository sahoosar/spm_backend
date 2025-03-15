--CREATE DATABASE IF NOT EXISTS spm_db;
--USE spm_db;
-- mysql user :mysql -u root
-- mysql user : mysql -u spm_admin -p
--pwd is : spm

-- Users Table
create TABLE IF NOT EXISTS users (
    user_id VARCHAR(36) PRIMARY KEY,
    user_name VARCHAR(60) UNIQUE NOT NULL,
    user_email VARCHAR(60) UNIQUE NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    password VARCHAR(300) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Profiles Table (1-to-1 Relationship with Users)
create TABLE IF NOT EXISTS profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    address VARCHAR(255),
    user_id VARCHAR(36) UNIQUE,  -- Ensures one profile per user
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON delete CASCADE
);

-- Roles Table
create TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL  -- Added role names like 'USER', 'ADMIN'
);

-- User Roles (Many-to-Many Relationship)
create TABLE IF NOT EXISTS user_roles (
    user_id VARCHAR(36),
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id),  -- Composite PK ensures unique pairs
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON delete CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON delete CASCADE
);

-- Portfolios Table (Each User Has One Portfolio)
create TABLE IF NOT EXISTS portfolios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,  --  Removed UNIQUE to allow multiple portfolios
    total_value DECIMAL(15,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON delete CASCADE
);

-- Holdings Table (Each Portfolio Can Have Multiple Holdings)
create TABLE IF NOT EXISTS holdings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    portfolio_id BIGINT NOT NULL,
    stock_symbol VARCHAR(10) NOT NULL,
    quantity INT NOT NULL,
    purchase_price DECIMAL(10,2) NOT NULL,
    current_price DECIMAL(10,2) NOT NULL,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,
    FOREIGN KEY (portfolio_id) REFERENCES portfolios(id) ON delete CASCADE
);

-- Transactions Table (Each Portfolio Can Have Many Transactions)
create TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    portfolio_id BIGINT NOT NULL,
    stock_symbol VARCHAR(10) NOT NULL,
    transaction_type VARCHAR(10) NOT NULL,  -- ✅ Replaced ENUM with VARCHAR for flexibility
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (portfolio_id) REFERENCES portfolios(id) ON delete CASCADE
);

-- Refresh Tokens Table
create TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,  -- Removed UNIQUE to allow multiple tokens per user
    token TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON delete CASCADE
);

-- Audit Logs Table (Logs User Actions)
create TABLE IF NOT EXISTS audit_logs (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(36),
    action VARCHAR(255) NOT NULL,  -- e.g., 'BUY ORDER PLACED', 'SELL ORDER EXECUTED'
    details TEXT NOT NULL,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP
);
--- Excuted below script

CREATE TABLE stock_list (
    stock_id SERIAL PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    stock_symbol VARCHAR(20) NOT NULL,
    open_price DECIMAL(10, 2),
    high_price DECIMAL(10, 2),
    low_price DECIMAL(10, 2),
    current_price DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE portfolios (
    portfolio_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    total_value DECIMAL(19,4) DEFAULT NULL,
    stock_symbol VARCHAR(50) DEFAULT NULL,
    buy_price DECIMAL(19,4) DEFAULT NULL,
    quantity INT DEFAULT NULL,
    current_price DECIMAL(19,4) DEFAULT NULL,
    profit_loss DECIMAL(19,4) DEFAULT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(user_id)
) ;

CREATE TABLE transactions_audit (
  transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(255) NOT NULL,
  stock_symbol VARCHAR(100) NOT NULL,
  operation_type VARCHAR(50) NOT NULL,
  quantity INT NOT NULL,
  price DECIMAL(19,2) NOT NULL,
  transaction_date DATETIME NOT NULL,
  CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(user_id)
);