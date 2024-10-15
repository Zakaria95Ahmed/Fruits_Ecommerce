package com.fruits.ecommerce.exceptions.products;


public class InvalidImageException extends RuntimeException {
    public InvalidImageException(String message) {
        super(message);
    }
}