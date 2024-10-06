package com.fruits.ecommerce.Controller;

import com.fruits.ecommerce.Models.DTOs.ProductDTO;
import com.fruits.ecommerce.Services.Interfaces.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Endpoint to create a new product (admin access)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO createdProduct = productService.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    // Public endpoint to list products with pagination
    @GetMapping
    public ResponseEntity<Page<ProductDTO>> listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        Page<ProductDTO> productsPage = productService.listProducts(page, pageSize);
        return ResponseEntity.ok(productsPage);
    }

    // Retrieve a single product
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        ProductDTO productDTO = productService.getProductById(id);
        return ResponseEntity.ok(productDTO);
    }

    @GetMapping("/{id}/image/base64")
    public ResponseEntity<String> getProductImageBase64(@PathVariable Long id) {
        String base64Image = productService.getProductImageBase64(id);
        if (base64Image != null) {
            return ResponseEntity.ok(base64Image);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
