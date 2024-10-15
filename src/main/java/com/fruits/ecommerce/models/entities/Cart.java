package com.fruits.ecommerce.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Assuming shipping cost and discount are BigDecimal values
    @Column(nullable = false)
    private BigDecimal shippingCost = BigDecimal.ZERO;

    // Percentage discount value (e.g., 2 for 2%)
    @Column(nullable = false)
    private BigDecimal discount = BigDecimal.ZERO;

    @OneToOne
    @JoinColumn(name = "customer_id", nullable = true)
    private Customer customer;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    // Methods to add items to cart
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
    }
}
