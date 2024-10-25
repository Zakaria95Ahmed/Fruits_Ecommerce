package com.fruits.ecommerce.models.dtos;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateAddressRequest {
    @Valid
    private AddressDTO billingAddress;

    @Valid
    private AddressDTO shippingAddress;
}
