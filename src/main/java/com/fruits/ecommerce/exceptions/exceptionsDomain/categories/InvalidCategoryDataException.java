package com.fruits.ecommerce.exceptions.exceptionsDomain.categories;


public class InvalidCategoryDataException extends RuntimeException {
    public InvalidCategoryDataException(String message, Exception e) {
        super(message);
    }
}