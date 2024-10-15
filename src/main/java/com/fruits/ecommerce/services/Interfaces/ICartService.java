package com.fruits.ecommerce.services.Interfaces;

import com.fruits.ecommerce.models.dtos.CartDTO;
import org.springframework.security.core.Authentication;

public interface ICartService {
    void addProductToCart(Authentication authentication, Long productId, int quantity);
    CartDTO getCartDetails(Authentication authentication);
}