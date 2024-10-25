package com.fruits.ecommerce.exceptions.exceptionsDomain.products;

public class ImageProcessingException extends RuntimeException {
    public ImageProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}