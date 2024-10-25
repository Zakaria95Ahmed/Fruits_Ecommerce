package com.fruits.ecommerce.repository;

import com.fruits.ecommerce.models.entities.Category;
import com.fruits.ecommerce.models.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category);
    List<Product> findByNameContainingIgnoreCase(String searchValue);
}
