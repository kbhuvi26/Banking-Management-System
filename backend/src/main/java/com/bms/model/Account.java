package com.bms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents one row of the "accounts" table.
 * BigDecimal is used for money instead of double, because double loses
 * precision on decimal values - never use double/float for currency.
 */
public class Account {

    private Integer accountId;
    private String accountNumber;
    private Integer userId;
    private String accountType; // "SAVINGS" or "CURRENT"
    private BigDecimal balance;
    private LocalDateTime createdAt;

    public Account() {
    }

    public Account(Integer accountId, String accountNumber, Integer userId,
                    String accountType, BigDecimal balance, LocalDateTime createdAt) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.userId = userId;
        this.accountType = accountType;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
