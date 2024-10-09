package com.fruits.ecommerce.exceptions.ExceptionsDomain;

import com.fruits.ecommerce.exceptions.Global.UserServiceException;

public class InvalidUserDataException extends UserServiceException {
    public InvalidUserDataException(String message) {
        super(message);
    }
}
