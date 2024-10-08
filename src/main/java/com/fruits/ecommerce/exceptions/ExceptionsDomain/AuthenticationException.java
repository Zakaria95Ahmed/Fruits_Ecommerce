package com.fruits.ecommerce.exceptions.ExceptionsDomain;

import com.fruits.ecommerce.exceptions.Global.UserServiceException;

// Authentication-related exceptions
public class AuthenticationException extends UserServiceException {
    public AuthenticationException(String message) {
        super(message);
    }
}
