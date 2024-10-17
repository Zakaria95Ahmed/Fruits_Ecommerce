package com.fruits.ecommerce.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeRequestDTO {
    @NotBlank(message = "The old password must be entered.")
    private String oldPassword;
    @NotBlank(message = "The new password must be entered.")
    @Size(min = 8, message = "The new password must be at least 8 characters long.")
    private String newPassword;

}


