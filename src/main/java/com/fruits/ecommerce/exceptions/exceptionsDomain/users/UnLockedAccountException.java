package com.fruits.ecommerce.exceptions.exceptionsDomain.users;

import com.fruits.ecommerce.exceptions.global.UserServiceException;

public class UnLockedAccountException extends UserServiceException {
    public UnLockedAccountException(String message) {
        super(message);
    }
}