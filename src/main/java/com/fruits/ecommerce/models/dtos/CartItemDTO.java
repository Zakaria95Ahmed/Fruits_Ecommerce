package com.fruits.ecommerce.models.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class CartItemDTO {
    //    private ProductDTO productId;
    private Long cartId;
    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private int quantity;
}


