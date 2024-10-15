package com.fruits.ecommerce.exceptions.ExceptionsDomain;

import com.fruits.ecommerce.exceptions.Global.UserServiceException;

public class UnLockedAccountException extends UserServiceException {
    public UnLockedAccountException(String message) {
        super(message);
    }
}