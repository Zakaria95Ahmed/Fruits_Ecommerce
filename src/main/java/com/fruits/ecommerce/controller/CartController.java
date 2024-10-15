package com.fruits.ecommerce.controller;

import com.fruits.ecommerce.models.dtos.CartDTO;
import com.fruits.ecommerce.services.Interfaces.ICartService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/customer/cart")
@RequiredArgsConstructor
public class CartController {
    private final ICartService cartService;
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'USER')")
    public ResponseEntity<Void> addProductToCart(
            @RequestParam @Positive Long productId,
            @RequestParam @Positive(message = "Quantity must be positive")
            @Max(value = 100, message = "Quantity cannot exceed 100") int quantity,
            Authentication authentication) {
        cartService.addProductToCart(authentication, productId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','USER')")
    public ResponseEntity<CartDTO> getCartDetails(Authentication authentication) {
        CartDTO cartDTO = cartService.getCartDetails(authentication);
        return ResponseEntity.ok(cartDTO);
    }
}