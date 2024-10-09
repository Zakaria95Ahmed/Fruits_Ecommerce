package com.fruits.ecommerce.repository;

import com.fruits.ecommerce.models.entities.Role;
import com.fruits.ecommerce.models.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(RoleType name);
}
