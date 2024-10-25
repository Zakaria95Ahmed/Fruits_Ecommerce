package com.fruits.ecommerce.exceptions.exceptionsDomain.users;
import com.fruits.ecommerce.exceptions.global.UserServiceException;


// Registration-related exceptions
public class UsernameExistException extends UserServiceException {
    public UsernameExistException(String message) {
        super(message);
    }
}
