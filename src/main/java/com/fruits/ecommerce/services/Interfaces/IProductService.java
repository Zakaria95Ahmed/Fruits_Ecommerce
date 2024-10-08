package com.fruits.ecommerce.services.Interfaces;

import com.fruits.ecommerce.exceptions.products.InvalidProductDataException;
import com.fruits.ecommerce.exceptions.products.ProductNotFoundException;
import com.fruits.ecommerce.models.dtos.ProductDTO;
import org.springframework.data.domain.Page;

public interface IProductService {

    ProductDTO createProduct(ProductDTO productDTO) throws InvalidProductDataException;

    Page<ProductDTO> listProducts(int page, int pageSize);

    ProductDTO getProductById(Long id) throws ProductNotFoundException;

    String getProductImageBase64(Long id) throws ProductNotFoundException;


    // Additional methods if needed
}

