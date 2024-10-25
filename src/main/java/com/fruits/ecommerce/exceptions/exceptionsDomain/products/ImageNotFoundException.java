package com.fruits.ecommerce.exceptions.exceptionsDomain.products;


public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException(String message) {
        super(message);
    }
}