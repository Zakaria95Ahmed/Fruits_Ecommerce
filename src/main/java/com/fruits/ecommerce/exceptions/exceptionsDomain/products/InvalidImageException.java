package com.fruits.ecommerce.exceptions.exceptionsDomain.products;


public class InvalidImageException extends RuntimeException {
    public InvalidImageException(String message) {
        super(message);
    }
}