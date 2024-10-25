package com.fruits.ecommerce.exceptions.exceptionsDomain.categories;


public class CategoryInUseException extends RuntimeException {
    public CategoryInUseException(String message) {
        super(message);
    }
}


