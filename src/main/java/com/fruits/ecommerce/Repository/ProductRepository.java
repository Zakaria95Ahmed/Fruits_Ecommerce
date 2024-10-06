package com.fruits.ecommerce.Repository;

import com.fruits.ecommerce.Models.Entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Additional query methods if needed
}
