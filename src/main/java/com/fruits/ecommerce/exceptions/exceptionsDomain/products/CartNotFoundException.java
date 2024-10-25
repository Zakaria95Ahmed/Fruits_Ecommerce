package com.fruits.ecommerce.exceptions.exceptionsDomain.products;


public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(String message) {
        super(message);
    }
}