package com.fruits.ecommerce.Models.Entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;


    @NotNull
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotNull
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotNull
    @Column(name = "user_name", unique = true, nullable = false)
    private String username;


    @NotNull
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email;

    @NotNull
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Column(name = "password", nullable = false)
    private String password;

    private String address;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    private boolean isActive;
    private boolean isNotLocked;


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.lastLogin = this.createdAt;
        this.updatedAt = this.createdAt;
        this.isActive = true;
        this.isNotLocked = true;
    }


    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
