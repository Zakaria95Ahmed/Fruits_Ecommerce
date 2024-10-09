package com.fruits.ecommerce.exceptions.ExceptionsDomain;

import com.fruits.ecommerce.exceptions.Global.UserServiceException;

public class BadCredentialsException extends UserServiceException {
    public BadCredentialsException(String message) {
        super(message);
    }
}
