package com.fruits.ecommerce.exceptions.ExceptionsDomain;

import com.fruits.ecommerce.exceptions.Global.UserServiceException;

public class CustomerNotFoundException extends UserServiceException {
    public CustomerNotFoundException(String message) {
        super(message);
    }
}