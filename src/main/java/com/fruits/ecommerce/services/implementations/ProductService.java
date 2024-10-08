package com.fruits.ecommerce.services.implementations;

import com.fruits.ecommerce.exceptions.products.InvalidProductDataException;
import com.fruits.ecommerce.exceptions.products.ProductNotFoundException;
import com.fruits.ecommerce.models.dtos.ProductDTO;
import com.fruits.ecommerce.models.entities.Product;
import com.fruits.ecommerce.models.mappers.ProductMapper;
import com.fruits.ecommerce.repository.ProductRepository;
import com.fruits.ecommerce.services.Interfaces.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    // Create a new Product
    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) throws InvalidProductDataException {
        // Validate the Entered product-data
        if (productDTO.getName() == null || productDTO.getPrice() == null) {
            throw new InvalidProductDataException("Product name and price are required.");
        }
        Product product = productMapper.toEntity(productDTO);
        product = productRepository.save(product);
        return productMapper.toDTO(product);
    }

    // Getting on products with pagination
    @Override
    public Page<ProductDTO> listProducts(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Product> productsPage = productRepository.findAll(pageable);
        return productsPage.map(productMapper::toDTO);
    }

    // get Product by id
    @Override
    public ProductDTO getProductById(Long id) throws ProductNotFoundException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return productMapper.toDTO(product);
    }

    // get Product-Image Base64
    @Override
    public String getProductImageBase64(Long id) throws ProductNotFoundException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        if (product.getImageData() != null) {
            return Base64.getEncoder().encodeToString(product.getImageData());
        } else {
            return null;
        }
    }
}
