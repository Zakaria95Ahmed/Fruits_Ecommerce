package com.fruits.ecommerce.repository;

import com.fruits.ecommerce.models.entities.Role;
import com.fruits.ecommerce.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User>findByUsernameOrEmail(String username, String email);
    List<User> findAllByRoles(Role role);

}
