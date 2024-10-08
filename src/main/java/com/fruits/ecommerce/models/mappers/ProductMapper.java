package com.fruits.ecommerce.models.mappers;

import com.fruits.ecommerce.models.dtos.ProductDTO;
import com.fruits.ecommerce.models.entities.Product;
import org.mapstruct.*;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "images", ignore = true)
    ProductDTO toDTO(Product product);

    @Mapping(target = "images", ignore = true)
    Product toEntity(ProductDTO productDTO);

    @AfterMapping
    default void mapImagesToDTO(Product product, @MappingTarget ProductDTO productDTO) {
        if (product.getImages() != null) {
            List<String> imageUrls = product.getImages().stream()
                    .map(ProductImage::getImageUrl)
                    .collect(Collectors.toList());
            productDTO.setImageUrls(imageUrls);
        }
    }
}

