package com.fruits.ecommerce.exceptions.exceptionsDomain.users;

import com.fruits.ecommerce.exceptions.global.UserServiceException;

public class AccountLockedException extends UserServiceException {
    public AccountLockedException(String message) {
        super(message);
    }
}
