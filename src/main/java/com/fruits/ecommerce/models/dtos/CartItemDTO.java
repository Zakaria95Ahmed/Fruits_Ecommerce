package com.fruits.ecommerce.models.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class CartItemDTO {
    private Long cartId,productId;
    private String productName;
    private BigDecimal productPrice;
    private int quantity;
}


