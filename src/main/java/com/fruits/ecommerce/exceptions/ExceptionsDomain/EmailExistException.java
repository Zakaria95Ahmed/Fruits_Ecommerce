package com.fruits.ecommerce.exceptions.ExceptionsDomain;

import com.fruits.ecommerce.exceptions.Global.UserServiceException;

public class EmailExistException extends UserServiceException {
    public EmailExistException(String message) {
        super(message);
    }
}
