package com.fruits.ecommerce.Models.Mappers;

import com.fruits.ecommerce.Models.DTOs.ProductDTO;
import com.fruits.ecommerce.Models.Entities.Product;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // Map basic fields
    @Mapping(target = "base64ImageData", ignore = true)
    ProductDTO toDTO(Product product);
    Product toEntity(ProductDTO productDTO);

    // After mapping, set the base64ImageData
    @AfterMapping
    default void encodeImage(Product product, @MappingTarget ProductDTO productDTO) {
        if (product.getImageData() != null) {
            try {
                Path imagePath = Paths.get("product-images").resolve(Arrays.toString(product.getImageData()));
                byte[] imageBytes = Files.readAllBytes(imagePath);
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                productDTO.setBase64ImageData(base64Image);
            } catch (IOException e) {
                // Handle the exception as needed
                productDTO.setBase64ImageData(null);
            }
        }
    }




}
