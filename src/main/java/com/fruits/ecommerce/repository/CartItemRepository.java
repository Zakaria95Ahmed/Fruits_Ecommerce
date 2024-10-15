package com.fruits.ecommerce.repository;

import com.fruits.ecommerce.models.entities.CartItem;
import com.fruits.ecommerce.models.entities.CartItemKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, CartItemKey> {
}
