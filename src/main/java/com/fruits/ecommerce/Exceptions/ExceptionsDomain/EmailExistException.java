package com.fruits.ecommerce.Exceptions.ExceptionsDomain;

import com.fruits.ecommerce.Exceptions.Global.UserServiceException;

public class EmailExistException extends UserServiceException {
    public EmailExistException(String message) {
        super(message);
    }
}
