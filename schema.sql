--CREATE DATABASE IF NOT EXISTS spm_db;
--USE spm_db;
-- mysql user :mysql -u root
-- mysql user : mysql -u spm_admin -p
--pwd is : spm

-- Users Table
CREATE TABLE `users` (
  `user_id` varchar(36) NOT NULL,
  `user_name` varchar(60) NOT NULL,
  `user_email` varchar(60) NOT NULL,
  `phone_number` varchar(15) NOT NULL,
  `password` varchar(300) NOT NULL,
  `active` tinyint(1) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_name` (`user_name`),
  UNIQUE KEY `user_email` (`user_email`)
)
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