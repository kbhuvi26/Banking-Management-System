package com.bms.exception;

/** Thrown when registering with a username/email that already exists. */
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
