package com.fruits.ecommerce.exceptions.exceptionsDomain.users;

import com.fruits.ecommerce.exceptions.global.UserServiceException;

public class InvalidUserDataException extends UserServiceException {
    public InvalidUserDataException(String message) {
        super(message);
    }
}
