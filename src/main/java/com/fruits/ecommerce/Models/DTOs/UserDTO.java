package com.fruits.ecommerce.Models.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Set;


@Setter
@Getter
public class UserDTO {

//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)// Comment this when use test
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50)
    private String firstName;

    @NotBlank(message = "last name is required")
    @Size(min = 2, max = 50)
    private String lastName;

    @NotBlank(message = "username is required")
    @Size(min = 4, max = 45,message = "Username must be between 4 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$") // Alphanumeric with underscore
    private String username;


//  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)  // Comment this when use test
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;


    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 200)
    private String address;


    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LocalDateTime lastLogin;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LocalDateTime updatedAt;

    private Set<String> roles;

    private boolean isActive;

    private boolean isNotLocked;
}
