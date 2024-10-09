package com.fruits.ecommerce.exceptions.ExceptionsDomain;

import com.fruits.ecommerce.exceptions.Global.UserServiceException;

public class AccountLockedException extends UserServiceException {
    public AccountLockedException(String message) {
        super(message);
    }
}
