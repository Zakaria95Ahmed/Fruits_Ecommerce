package com.fruits.ecommerce.models.mappers;

import com.fruits.ecommerce.models.dtos.CartItemDTO;
import com.fruits.ecommerce.models.entities.CartItem;
import com.fruits.ecommerce.models.entities.ProductImage;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface CartItemMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.price", target = "productPrice")
    @Mapping(source = "cart.id", target = "cartId")
    CartItemDTO toDTO(CartItem cartItem);

    @InheritInverseConfiguration
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cart", ignore = true)
    CartItem toEntity(CartItemDTO cartItemDTO);

    List<CartItemDTO> toDTOList(List<CartItem> cartItems);
    List<CartItem> toEntityList(List<CartItemDTO> cartItemDTOs);
    @AfterMapping
    default void linkCartItem(@MappingTarget CartItem cartItem) {
        if (cartItem.getProduct() != null) {
            cartItem.getId().setProductId(cartItem.getProduct().getId());
        }
    }

    // Add this method to handle the conversion from List<String> to List<ProductImage>
    default List<ProductImage> mapStringToProductImage(List<String> imageUrls) {
        if (imageUrls == null) {
            return null;
        }
        return imageUrls.stream()
                .map(url -> {
                    ProductImage productImage = new ProductImage();
                    productImage.setImageUrl(url);
                    return productImage;
                })
                .collect(Collectors.toList());
    }

    // Add this method to handle the conversion from List<ProductImage> to List<String>
    default List<String> mapProductImageToString(List<ProductImage> productImages) {
        if (productImages == null) {
            return null;
        }
        return productImages.stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());
    }

}