package com.fruits.ecommerce.Models.DTOs;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequestDTO {
    private String username,email,password;
}
