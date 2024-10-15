package com.fruits.ecommerce.exceptions.products;


public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException(String message) {
        super(message);
    }
}