package com.fruits.ecommerce.models.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CartDTO {
    private Long id, customerId;
    private BigDecimal shippingCost;
    private BigDecimal discount;
    private List<CartItemDTO> items;
}