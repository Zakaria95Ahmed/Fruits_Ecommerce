package com.fruits.ecommerce.Exceptions.ExceptionsDomain;

import com.fruits.ecommerce.Exceptions.Global.UserServiceException;

// Authentication-related exceptions
public class AuthenticationException extends UserServiceException {
    public AuthenticationException(String message) {
        super(message);
    }
}
