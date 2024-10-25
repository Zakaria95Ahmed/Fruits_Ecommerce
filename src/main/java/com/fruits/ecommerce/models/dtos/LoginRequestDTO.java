package com.fruits.ecommerce.models.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequestDTO {
    private String username;
    private String email;
    private String password;

}
