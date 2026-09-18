package com.bms.exception;

/** Thrown when a withdrawal or transfer amount is greater than the account balance. */
public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
