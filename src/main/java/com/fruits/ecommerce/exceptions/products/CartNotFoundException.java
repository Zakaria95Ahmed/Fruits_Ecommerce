package com.fruits.ecommerce.exceptions.products;


public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(String message) {
        super(message);
    }
}