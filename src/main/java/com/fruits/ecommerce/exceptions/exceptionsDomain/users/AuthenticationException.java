package com.fruits.ecommerce.exceptions.exceptionsDomain.users;

import com.fruits.ecommerce.exceptions.global.UserServiceException;

// Authentication-related exceptions
public class AuthenticationException extends UserServiceException {
    public AuthenticationException(String message) {
        super(message);
    }
}
