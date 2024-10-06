package com.fruits.ecommerce.Exceptions.ExceptionsDomain;

import com.fruits.ecommerce.Exceptions.Global.UserServiceException;

public class InvalidUserDataException extends UserServiceException {
    public InvalidUserDataException(String message) {
        super(message);
    }
}
