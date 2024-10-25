package com.fruits.ecommerce.exceptions.exceptionsDomain.users;

import com.fruits.ecommerce.exceptions.global.UserServiceException;

public class EmailExistException extends UserServiceException {
    public EmailExistException(String message) {
        super(message);
    }
}
