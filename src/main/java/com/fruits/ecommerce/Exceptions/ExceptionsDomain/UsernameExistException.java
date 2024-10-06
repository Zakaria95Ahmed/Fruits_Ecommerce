package com.fruits.ecommerce.Exceptions.ExceptionsDomain;
import com.fruits.ecommerce.Exceptions.Global.UserServiceException;


// Registration-related exceptions
public class UsernameExistException extends UserServiceException {
    public UsernameExistException(String message) {
        super(message);
    }
}
