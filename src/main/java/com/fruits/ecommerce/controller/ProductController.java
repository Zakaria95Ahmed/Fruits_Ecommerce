package com.fruits.ecommerce.controller;

import com.fruits.ecommerce.models.dtos.ProductDTO;
import com.fruits.ecommerce.models.dtos.ProductImageDTO;
import com.fruits.ecommerce.services.Interfaces.IProductService;
import com.fruits.ecommerce.services.Utils.ImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;
    private final ImageService imageService;

    // Create a new product (ADMIN Access only)
    @PostMapping("/admin")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO createdProduct = productService.createProduct(productDTO);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    // Get a list of products with pagination (Available to Everyone)
    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<ProductDTO> products = productService.listProducts(page, pageSize);
        return ResponseEntity.ok(products);
    }

    // Get details of a specific product
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        ProductDTO productDTO = productService.getProductById(id);
        return ResponseEntity.ok(productDTO);
    }

    // Upload independent images (ADMIN Access only)
    @PostMapping("/admin/images/upload")
    public ResponseEntity<List<ProductImageDTO>> uploadImages(
            @RequestParam("images") List<MultipartFile> images) throws IOException {
        List<ProductImageDTO> imageDTOs = productService.uploadImages(images);
        return ResponseEntity.ok(imageDTOs);
    }

    // Get an image using the ID
    @GetMapping("/images/id/{id}")
    public ResponseEntity<Resource> getImageById(@PathVariable Long id) throws IOException {
        return productService.getImageResponseById(id);
    }

    // Get an image using the file name
    @GetMapping("/images/file/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Resource resource = imageService.loadImageAsResource(filename);
            return ResponseEntity.ok()
                    .contentType(MediaTypeFactory.getMediaType(filename).orElse(MediaType.APPLICATION_OCTET_STREAM))
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // Link existing images to a specific product (ADMIN Access only)
    @PutMapping("/admin/{productId}/images/link")
    public ResponseEntity<ProductDTO> linkImagesToProduct(
            @PathVariable Long productId,
            @RequestBody List<Long> imageIds) {
        ProductDTO productDTO = productService.linkImagesToProduct(productId, imageIds);
        return ResponseEntity.ok(productDTO);
    }


    // Upload new images and link them to a product (ADMIN Access only)
    @PostMapping("/admin/{productId}/images")
    public ResponseEntity<List<ProductImageDTO>> addImagesToProduct(
            @PathVariable Long productId,
            @RequestParam("images") List<MultipartFile> images) throws IOException {
        List<ProductImageDTO> imageDTOs = productService.addImagesToProduct(productId, images);
        return ResponseEntity.ok(imageDTOs);
    }


    // Delete an image(ADMIN Access only)
    @DeleteMapping("/admin/images/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) throws IOException {
        productService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }


    // Update a product (ADMIN Access only)
    @PutMapping("/admin/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }


    // Delete a product (ADMIN Access only)
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/byCategory/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductDTO> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String query) {
        List<ProductDTO> products = productService.searchProduct(query);
        return ResponseEntity.ok(products);
    }

}


