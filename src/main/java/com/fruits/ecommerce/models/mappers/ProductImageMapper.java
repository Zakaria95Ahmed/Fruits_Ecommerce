package com.fruits.ecommerce.models.mappers;

import com.fruits.ecommerce.models.dtos.ProductImageDTO;
import com.fruits.ecommerce.models.entities.ProductImage;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface ProductImageMapper {
    ProductImageDTO toDTO(ProductImage productImage);
    List<ProductImageDTO> toDTOList(List<ProductImage> productImages);
    ProductImage toEntity(ProductImageDTO productImageDTO);
}