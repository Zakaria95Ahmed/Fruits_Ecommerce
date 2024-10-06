package com.fruits.ecommerce.Exceptions.ExceptionsDomain;

import com.fruits.ecommerce.Exceptions.Global.UserServiceException;

public class AccountLockedException extends UserServiceException {
    public AccountLockedException(String message) {
        super(message);
    }
}
