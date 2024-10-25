package com.fruits.ecommerce.exceptions.exceptionsDomain.users;

import com.fruits.ecommerce.exceptions.global.UserServiceException;

public class InvalidTokenException extends UserServiceException {
    public InvalidTokenException(String message) {
        super(message);
    }
}