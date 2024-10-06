package com.fruits.ecommerce.Services.Implementations;

import com.fruits.ecommerce.Models.DTOs.ProductDTO;
import com.fruits.ecommerce.Models.Entities.Product;
import com.fruits.ecommerce.Models.Mappers.ProductMapper;
import com.fruits.ecommerce.Repository.ProductRepository;
import com.fruits.ecommerce.Services.Interfaces.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = productMapper.toEntity(productDTO);
        // Handle image upload if necessary
        product = productRepository.save(product);
        return productMapper.toDTO(product);
    }

    @Override
    public Page<ProductDTO> listProducts(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(product -> {
            ProductDTO productDTO = productMapper.toDTO(product);
            // Optionally encode the image data if required
            encodeProductImage(product, productDTO);
            return productDTO;
        });
    }

    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        ProductDTO productDTO = productMapper.toDTO(product);
        encodeProductImage(product, productDTO);
        return productDTO;
    }

    @Override
    public String getProductImageBase64(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.getImageData() != null) {
            return Base64.getEncoder().encodeToString(product.getImageData());
        } else {
            return null;
        }
    }

    private void encodeProductImage(Product product, ProductDTO productDTO) {
        if (product.getImageData() != null) {
            String base64Image = Base64.getEncoder().encodeToString(product.getImageData());
            productDTO.setBase64ImageData(base64Image);
        } else {
            productDTO.setBase64ImageData(null);
        }
    }

}
