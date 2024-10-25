package com.fruits.ecommerce.services.Interfaces;

import com.fruits.ecommerce.models.dtos.AddressDTO;
import com.fruits.ecommerce.models.dtos.CartDTO;
import org.springframework.security.core.Authentication;

public interface ICartService {
    void addProductToCart(Authentication authentication, Long productId, int quantity);
    void updateCustomerAddresses(Authentication authentication, AddressDTO billingAddress, AddressDTO shippingAddress);
    CartDTO getCartDetails(Authentication authentication);
}