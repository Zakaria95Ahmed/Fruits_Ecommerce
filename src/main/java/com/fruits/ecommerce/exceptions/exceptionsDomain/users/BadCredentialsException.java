package com.fruits.ecommerce.exceptions.exceptionsDomain.users;

import com.fruits.ecommerce.exceptions.global.UserServiceException;

public class BadCredentialsException extends UserServiceException {
    public BadCredentialsException(String message) {
        super(message);
    }
}
