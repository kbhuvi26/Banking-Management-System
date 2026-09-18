-- =====================================================================
-- Banking Management System - MySQL Schema
-- Run this whole file in MySQL Workbench / mysql CLI BEFORE starting
-- the Spring Boot application.
-- =====================================================================

DROP DATABASE IF EXISTS banking_management_system;
CREATE DATABASE banking_management_system;
USE banking_management_system;

-- ---------------------------------------------------------------------
-- users: one row per registered person
-- ---------------------------------------------------------------------
CREATE TABLE users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100)  NOT NULL,
    email         VARCHAR(100)  NOT NULL,
    phone_number  VARCHAR(15)   NOT NULL,
    username      VARCHAR(50)   NOT NULL,
    password      VARCHAR(255)  NOT NULL,   -- stored as a SHA-256 hash, never plain text
    created_at    TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_users_email    UNIQUE (email),
    CONSTRAINT uq_users_username UNIQUE (username)
);

-- ---------------------------------------------------------------------
-- accounts: each user can have one or more bank accounts
-- ---------------------------------------------------------------------
CREATE TABLE accounts (
    account_id      INT AUTO_INCREMENT PRIMARY KEY,
    account_number  VARCHAR(20)     NOT NULL,
    user_id         INT             NOT NULL,
    account_type    ENUM('SAVINGS','CURRENT') NOT NULL,
    balance         DECIMAL(15,2)   NOT NULL DEFAULT 0.00,
    created_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_accounts_number UNIQUE (account_number),
    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id)
        REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT chk_balance_non_negative CHECK (balance >= 0)
);

-- ---------------------------------------------------------------------
-- transactions: every deposit / withdrawal / transfer leg is one row
-- ---------------------------------------------------------------------
CREATE TABLE transactions (
    transaction_id       INT AUTO_INCREMENT PRIMARY KEY,
    account_id           INT             NOT NULL,
    transaction_type     ENUM('DEPOSIT','WITHDRAW','TRANSFER_IN','TRANSFER_OUT') NOT NULL,
    amount                DECIMAL(15,2)  NOT NULL,
    balance_after         DECIMAL(15,2)  NOT NULL,
    related_account_id    INT            NULL,   -- the other account, only for transfers
    description           VARCHAR(255),
    transaction_date      TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_txn_account FOREIGN KEY (account_id)
        REFERENCES accounts(account_id) ON DELETE CASCADE,
    CONSTRAINT fk_txn_related_account FOREIGN KEY (related_account_id)
        REFERENCES accounts(account_id) ON DELETE SET NULL,
    CONSTRAINT chk_amount_positive CHECK (amount > 0)
);

-- Helpful indexes for common lookups
CREATE INDEX idx_accounts_user_id ON accounts(user_id);
CREATE INDEX idx_txn_account_id ON transactions(account_id);
CREATE INDEX idx_txn_date ON transactions(transaction_date);
