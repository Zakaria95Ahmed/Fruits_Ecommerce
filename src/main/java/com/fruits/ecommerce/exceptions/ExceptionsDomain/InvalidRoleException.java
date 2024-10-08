package com.fruits.ecommerce.exceptions.ExceptionsDomain;

import com.fruits.ecommerce.exceptions.Global.UserServiceException;

public class InvalidRoleException extends UserServiceException {
    public InvalidRoleException(String message) {
        super(message);
    }
}
