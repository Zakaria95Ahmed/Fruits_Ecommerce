package com.fruits.ecommerce.services.Interfaces;

import com.fruits.ecommerce.exceptions.products.ImageNotFoundException;
import com.fruits.ecommerce.exceptions.products.InvalidImageException;
import com.fruits.ecommerce.exceptions.products.InvalidProductDataException;
import com.fruits.ecommerce.exceptions.products.ProductNotFoundException;
import com.fruits.ecommerce.models.dtos.ProductDTO;
import com.fruits.ecommerce.models.dtos.ProductImageDTO;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IProductService {

    ProductDTO createProduct(ProductDTO productDTO) throws InvalidProductDataException;
    Page<ProductDTO> listProducts(int page, int pageSize);
    ProductDTO getProductById(Long id) throws ProductNotFoundException;
    List<ProductImageDTO> addImagesToProduct(Long productId, List<MultipartFile> images)
            throws ProductNotFoundException, InvalidImageException, IOException;
    void deleteImage(Long imageId) throws ImageNotFoundException, IOException;

//    Resource getImageResourceById(Long imageId) throws ImageNotFoundException, IOException;
    ResponseEntity<Resource> getImageResponseById(Long id) throws ImageNotFoundException, IOException;

    // Additional methods if needed
    List<ProductImageDTO> uploadImages(List<MultipartFile> images) throws InvalidImageException, IOException;

    ProductDTO linkImagesToProduct(Long productId, List<Long> imageIds) throws ProductNotFoundException,
            ImageNotFoundException;
    ProductDTO updateProduct(Long id, ProductDTO productDTO) throws ProductNotFoundException, InvalidProductDataException;
    void deleteProduct(Long id) throws ProductNotFoundException;


   }

