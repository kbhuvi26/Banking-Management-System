package com.bms.exception;

/** Thrown when a user/account/transaction id does not exist in the database. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
