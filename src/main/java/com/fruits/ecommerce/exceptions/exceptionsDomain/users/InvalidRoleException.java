package com.fruits.ecommerce.exceptions.exceptionsDomain.users;

import com.fruits.ecommerce.exceptions.global.UserServiceException;

public class InvalidRoleException extends UserServiceException {
    public InvalidRoleException(String message) {
        super(message);
    }
}
