package com.fruits.ecommerce.Exceptions.ExceptionsDomain;

import com.fruits.ecommerce.Exceptions.Global.UserServiceException;

public class InvalidRoleException extends UserServiceException {
    public InvalidRoleException(String message) {
        super(message);
    }
}
