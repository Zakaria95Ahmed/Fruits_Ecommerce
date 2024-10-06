package com.fruits.ecommerce.Exceptions.ExceptionsDomain;

import com.fruits.ecommerce.Exceptions.Global.UserServiceException;

public class BadCredentialsException extends UserServiceException {
    public BadCredentialsException(String message) {
        super(message);
    }
}
