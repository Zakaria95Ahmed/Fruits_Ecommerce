package com.fruits.ecommerce.exceptions.global;

// Base exception class for all user-related exceptions
public abstract class UserServiceException extends RuntimeException {
    public UserServiceException(String message) {
        super(message);
    }
}
