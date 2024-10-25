package com.fruits.ecommerce.exceptions.exceptionsDomain.users;

import com.fruits.ecommerce.exceptions.global.UserServiceException;

public class CustomerNotFoundException extends UserServiceException {
    public CustomerNotFoundException(String message) {
        super(message);
    }
}