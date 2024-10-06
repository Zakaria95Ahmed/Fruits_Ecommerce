package com.fruits.ecommerce.Models.Entities;

import com.fruits.ecommerce.Models.Enum.RoleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


@NoArgsConstructor
@AllArgsConstructor
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

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

}
