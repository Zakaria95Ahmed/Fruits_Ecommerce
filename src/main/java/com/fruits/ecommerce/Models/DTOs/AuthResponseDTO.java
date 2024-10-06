package com.fruits.ecommerce.Models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private UserDTO user;
    public AuthResponseDTO() {

    }
}
