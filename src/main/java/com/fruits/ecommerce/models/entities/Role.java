package com.fruits.ecommerce.models.entities;

import com.fruits.ecommerce.models.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;



@Setter
@Getter
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 25, unique = true, nullable = false)
    private RoleType name;

// To avoid NullPointerException
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    public Role() {}

    public Role(RoleType name) {
        this.name = name;
    }

    public static Role of(RoleType name) {
        return new Role(name);
    }
}
