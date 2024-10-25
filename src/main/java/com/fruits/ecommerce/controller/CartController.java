package com.fruits.ecommerce.controller;

import com.fruits.ecommerce.models.dtos.CartDTO;
import com.fruits.ecommerce.models.dtos.UpdateAddressRequest;
import com.fruits.ecommerce.services.Interfaces.ICartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/customer/cart")
@RequiredArgsConstructor
public class CartController {
    private final ICartService cartService;

    // User Or Customer (Access-Only)
    @PostMapping("/add")
    public ResponseEntity<Void> addProductToCart(
            @RequestParam @Positive Long productId,
            @RequestParam @Positive(message = "Quantity must be positive")
            @Max(value = 100, message = "Quantity cannot exceed 100") int quantity,
            Authentication authentication) {
        cartService.addProductToCart(authentication, productId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Customer (Access-Only)
    @PutMapping("/addresses")
    public ResponseEntity<Void> updateAddresses(
            @Valid @RequestBody UpdateAddressRequest request,
            Authentication authentication) {
        log.info("Received address update request: {}", request);  // إضافة logging
        cartService.updateCustomerAddresses(authentication, request.getBillingAddress(), request.getShippingAddress());
        return ResponseEntity.ok().build();
    }

    // Customer (Access-Only)
    @GetMapping
    public ResponseEntity<CartDTO> getCartDetails(Authentication authentication) {
        CartDTO cartDTO = cartService.getCartDetails(authentication);
        return ResponseEntity.ok(cartDTO);
    }

}