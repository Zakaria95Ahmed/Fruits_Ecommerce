package com.fruits.ecommerce.Exceptions.ExceptionsDomain;

import com.fruits.ecommerce.Exceptions.Global.UserServiceException;

public class UnLockedAccountException extends UserServiceException {
    public UnLockedAccountException(String message) {
        super(message);
    }
}