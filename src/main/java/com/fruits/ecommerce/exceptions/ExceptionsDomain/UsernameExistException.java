package com.fruits.ecommerce.exceptions.ExceptionsDomain;
import com.fruits.ecommerce.exceptions.Global.UserServiceException;


// Registration-related exceptions
public class UsernameExistException extends UserServiceException {
    public UsernameExistException(String message) {
        super(message);
    }
}
