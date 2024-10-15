package com.fruits.ecommerce.models.mappers;

import com.fruits.ecommerce.models.dtos.CartDTO;
import com.fruits.ecommerce.models.entities.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;


@Mapper(componentModel = "spring", uses = {CartItemMapper.class}, imports = BigDecimal.class)
public interface CartMapper {
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "items", target = "items")
    @Mapping(target = "shippingCost", defaultExpression = "java(cart.getShippingCost() != null ? cart.getShippingCost() : BigDecimal.ZERO)")
    @Mapping(target = "discount", defaultExpression = "java(cart.getDiscount() != null ? cart.getDiscount() : BigDecimal.ZERO)")
    CartDTO toDTO(Cart cart);
    @Mapping(target = "customer", ignore = true)
    Cart toEntity(CartDTO cartDTO);
}