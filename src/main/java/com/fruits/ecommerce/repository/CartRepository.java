package com.fruits.ecommerce.repository;

import com.fruits.ecommerce.models.entities.Cart;
import com.fruits.ecommerce.models.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByCustomer(Customer customer);
    @Query("SELECT c FROM Cart c " +
            "JOIN FETCH c.customer cust " +
            "JOIN FETCH cust.user u " +
            "LEFT JOIN FETCH c.items i " +
            "LEFT JOIN FETCH i.product " +
            "WHERE u.username = :username")
    Optional<Cart> findByCustomerUsername(@Param("username") String username);
}