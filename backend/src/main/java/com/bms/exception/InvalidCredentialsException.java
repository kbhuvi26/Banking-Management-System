package com.bms.exception;

/** Thrown at login when the username/email or password does not match. */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
