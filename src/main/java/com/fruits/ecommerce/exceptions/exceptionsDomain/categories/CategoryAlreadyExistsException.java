package com.fruits.ecommerce.exceptions.exceptionsDomain.categories;

public class CategoryAlreadyExistsException extends RuntimeException {
    public CategoryAlreadyExistsException(String message) {
        super(message);
    }
}


