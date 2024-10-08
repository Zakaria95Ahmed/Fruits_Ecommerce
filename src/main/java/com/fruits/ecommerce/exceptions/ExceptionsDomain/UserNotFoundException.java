package com.fruits.ecommerce.exceptions.ExceptionsDomain;

import com.fruits.ecommerce.exceptions.Global.UserServiceException;

public class UserNotFoundException extends UserServiceException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
