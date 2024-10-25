package com.fruits.ecommerce.services.Interfaces;

import com.fruits.ecommerce.exceptions.exceptionsDomain.products.ImageNotFoundException;
import com.fruits.ecommerce.exceptions.exceptionsDomain.products.InvalidImageException;
import com.fruits.ecommerce.exceptions.exceptionsDomain.products.InvalidProductDataException;
import com.fruits.ecommerce.exceptions.global.ResourceNotFoundException;
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
    ProductDTO getProductById(Long id) throws ResourceNotFoundException;
    List<ProductImageDTO> addImagesToProduct(Long productId, List<MultipartFile> images)
            throws ResourceNotFoundException, InvalidImageException, IOException;
    void deleteImage(Long imageId) throws ImageNotFoundException, IOException;

//    Resource getImageResourceById(Long imageId) throws ImageNotFoundException, IOException;
    ResponseEntity<Resource> getImageResponseById(Long id) throws ImageNotFoundException, IOException;

    // Additional methods if needed
    List<ProductImageDTO> uploadImages(List<MultipartFile> images) throws InvalidImageException, IOException;

    ProductDTO linkImagesToProduct(Long productId, List<Long> imageIds) throws ResourceNotFoundException,
            ImageNotFoundException;
    ProductDTO updateProduct(Long id, ProductDTO productDTO) throws ResourceNotFoundException, InvalidProductDataException;
    void deleteProduct(Long id) throws ResourceNotFoundException;


    /**
     * Retrieves a list of products by category.
     * @param categoryId The category identifier
     * @return A list of ProductDTO for the products in the specified category
     * @throws ResourceNotFoundException If the category is not found
     */

    List<ProductDTO> getProductsByCategory(Long categoryId) throws ResourceNotFoundException;

    /**
     * Searches for products using the search value.
     * @param searchValue The search value
     * @return A list of ProductDTO for the products that match the search value
     */
    List<ProductDTO> searchProduct(String searchValue);

   }

