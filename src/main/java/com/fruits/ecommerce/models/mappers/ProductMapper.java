package com.fruits.ecommerce.models.mappers;

import com.fruits.ecommerce.models.dtos.ProductDTO;
import com.fruits.ecommerce.models.entities.Product;
import com.fruits.ecommerce.models.entities.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {ProductImageMapper.class})
public interface ProductMapper {

    @Mapping(target = "imageUrls", source = "imageUrls", qualifiedByName = "imagesToUrls")
    ProductDTO toDTO(Product product);
    // We ignore the images when converting from DTO to Entity
    @Mapping(target = "imageUrls", ignore = true)
    Product toEntity(ProductDTO productDTO);

    @Named("imagesToUrls")
    default List<String> imagesToUrls(List<ProductImage> images) {
        if (images == null) {
            return null;
        }
        return images.stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());
    }

    @Named("urlsToImages")
    default List<ProductImage> urlsToImages(List<String> imageUrls) {
        if (imageUrls == null) {
            return null;
        }
        return imageUrls.stream()
                .map(url -> {
                    ProductImage image = new ProductImage();
                    image.setImageUrl(url);
                    return image;
                })
                .collect(Collectors.toList());
    }
}
