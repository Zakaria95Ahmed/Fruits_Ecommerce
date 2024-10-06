package com.fruits.ecommerce.Repository;

import com.fruits.ecommerce.Models.Entities.Role;
import com.fruits.ecommerce.Models.Enum.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(RoleType name);
}
