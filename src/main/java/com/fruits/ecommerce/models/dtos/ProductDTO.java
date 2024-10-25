package com.fruits.ecommerce.models.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    @NotBlank(message = "Product name is required")
    private String name;
    private String unit;
    @NotNull(message = "Price is required")
    private BigDecimal price;
    private String description;
    private List<String> imageUrls  = new ArrayList<>();
    private Long categoryId;
    private String categoryName;
    // Timing fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


