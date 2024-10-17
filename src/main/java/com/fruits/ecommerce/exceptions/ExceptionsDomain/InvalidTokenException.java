package com.fruits.ecommerce.exceptions.ExceptionsDomain;

import com.fruits.ecommerce.exceptions.Global.UserServiceException;

public class InvalidTokenException extends UserServiceException {
    public InvalidTokenException(String message) {
        super(message);
    }
}