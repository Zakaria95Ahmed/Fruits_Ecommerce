package com.fruits.ecommerce.models.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ProductDTO {

    private Long id;

    @NotBlank(message = "Product name is required")
    private String name;

    private String unit;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    private String description;

    // Add a field for base64 encoded image data
    // Include only when not null
//    private String base64ImageData;

    // Timing fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<String> imageUrls = new ArrayList<>();


}



