package com.bms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents one row of the "transactions" table.
 * transactionType is one of: DEPOSIT, WITHDRAW, TRANSFER_IN, TRANSFER_OUT.
 * A transfer between two accounts creates TWO rows: a TRANSFER_OUT on the
 * sender's account and a TRANSFER_IN on the receiver's account, linked
 * through relatedAccountId.
 */
public class Transaction {

    private Integer transactionId;
    private Integer accountId;
    private String transactionType;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private Integer relatedAccountId;
    private String description;
    private LocalDateTime transactionDate;

    public Transaction() {
    }

    public Transaction(Integer transactionId, Integer accountId, String transactionType,
                        BigDecimal amount, BigDecimal balanceAfter, Integer relatedAccountId,
                        String description, LocalDateTime transactionDate) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.relatedAccountId = relatedAccountId;
        this.description = description;
        this.transactionDate = transactionDate;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public Integer getRelatedAccountId() {
        return relatedAccountId;
    }

    public void setRelatedAccountId(Integer relatedAccountId) {
        this.relatedAccountId = relatedAccountId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}
