package com.fruits.ecommerce.models.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Long id;
    private UserDTO user;
    private AddressDTO billingAddress;
    private AddressDTO shippingAddress;
    private boolean isActive;
    private boolean isNotLocked;
    private LocalDateTime deletedAt;

}
