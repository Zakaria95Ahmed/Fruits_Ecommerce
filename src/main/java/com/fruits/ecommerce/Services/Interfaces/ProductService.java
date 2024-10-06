package com.fruits.ecommerce.Services.Interfaces;

import com.fruits.ecommerce.Models.DTOs.ProductDTO;
import org.springframework.data.domain.Page;

public interface ProductService {
    ProductDTO createProduct(ProductDTO productDTO);

    Page<ProductDTO> listProducts(int page, int pageSize);

    ProductDTO getProductById(Long id);

    String getProductImageBase64(Long id);

    // Additional methods if needed
}
