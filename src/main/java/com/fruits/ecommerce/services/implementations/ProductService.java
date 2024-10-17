package com.fruits.ecommerce.services.implementations;

import com.fruits.ecommerce.exceptions.products.*;
import com.fruits.ecommerce.models.dtos.ProductDTO;
import com.fruits.ecommerce.models.dtos.ProductImageDTO;
import com.fruits.ecommerce.models.entities.Product;
import com.fruits.ecommerce.models.entities.ProductImage;
import com.fruits.ecommerce.models.mappers.ProductImageMapper;
import com.fruits.ecommerce.models.mappers.ProductMapper;
import com.fruits.ecommerce.repository.ProductImageRepository;
import com.fruits.ecommerce.repository.ProductRepository;
import com.fruits.ecommerce.services.Interfaces.IProductService;
import com.fruits.ecommerce.services.Utils.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;
    private final ImageService imageService;

    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) throws InvalidProductDataException {
        validateProductData(productDTO);
        Product product = productMapper.toEntity(productDTO);
        product = productRepository.save(product);
        return productMapper.toDTO(product);
    }

    private void validateProductData(ProductDTO productDTO) throws InvalidProductDataException {
        if (productDTO.getName() == null || productDTO.getName().isEmpty()) {
            throw new InvalidProductDataException("Product name is required.");
        }
        if (productDTO.getPrice() == null) {
            throw new InvalidProductDataException("Product price is required.");
        }
    }

    @Override
    public Page<ProductDTO> listProducts(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return productRepository.findAll(pageable).map(productMapper::toDTO);
    }

    @Override
    public ProductDTO getProductById(Long id) throws ProductNotFoundException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
        return productMapper.toDTO(product);
    }

    @Override
    @Transactional
    public List<ProductImageDTO> addImagesToProduct(Long productId, List<MultipartFile> images)
            throws ProductNotFoundException, InvalidImageException, IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        List<ProductImage> newImages = new ArrayList<>();

        for (MultipartFile image : images) {
            ProductImage productImage = imageService.saveImage(image);
            productImage.setProduct(product);
            newImages.add(productImage);
        }

        productImageRepository.saveAll(newImages);

        return productImageMapper.toDTOList(newImages);
    }

    @Override
    public ResponseEntity<Resource> getImageResponseById(Long id) {
        try {
            ProductImage image = productImageRepository.findById(id)
                    .orElseThrow(() -> new ImageNotFoundException("Image not found with ID: " + id));

            // We use filePath to access the file directly.
            Resource resource = imageService.loadImageByPath(image.getFilePath());
            String contentType = determineContentType(resource);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (ImageNotFoundException | FileNotFoundException e) {
            log.error("Image not found with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            log.error("IO Error when getting image by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String determineContentType(Resource resource) {
        try {
            String contentType = Files.probeContentType(resource.getFile().toPath());
            return contentType != null ? contentType : "application/octet-stream";
        } catch (IOException e) {
            log.warn("Could not determine file type.", e);
            return "application/octet-stream";
        }
    }

    @Override
    @Transactional
    public List<ProductImageDTO> uploadImages(List<MultipartFile> images)
            throws InvalidImageException, IOException {
        List<ProductImage> newImages = new ArrayList<>();
        for (MultipartFile image : images) {
            ProductImage productImage = imageService.saveImage(image);
            // We don't associate it with any product here
            newImages.add(productImage);
        }
        productImageRepository.saveAll(newImages);
        return productImageMapper.toDTOList(newImages);
    }

    @Override
    @Transactional
    public ProductDTO linkImagesToProduct(Long productId, List<Long> imageIds)
            throws ProductNotFoundException, ImageNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        List<ProductImage> images = productImageRepository.findAllById(imageIds);
        if (images.size() != imageIds.size()) {
            throw new ImageNotFoundException("One or more images not found");
        }

        for (ProductImage image : images) {
            image.setProduct(product);
        }
        productImageRepository.saveAll(images);
        return productMapper.toDTO(product);
    }


    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) throws ProductNotFoundException, InvalidProductDataException {
        log.info("Updating product with ID: {}", id);
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

        validateProductData(productDTO);

        // Update properties
        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        //More properties can be added as needed.

        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Product updated successfully. ID: {}", updatedProduct.getId());
        return productMapper.toDTO(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) throws ProductNotFoundException {
        log.info("Deleting product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

// Delete images associated with the product
        List<ProductImage> images = productImageRepository.findByProductId(id);
        for (ProductImage image : images) {
            try {
                imageService.deleteImageByPath(image.getFilePath());
            } catch (IOException e) {
                log.error("Failed to delete image file for product ID: {}, Image ID: {}", id, image.getId(), e);
                // Continue deleting even if some images fail to delete
            }
        }
        productImageRepository.deleteAll(images);

        //finally delete the Product
        productRepository.delete(product);
        log.info("Product and associated images deleted successfully. ID: {}", id);
    }


    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        try {
            ProductImage image = productImageRepository.findById(imageId)
                    .orElseThrow(() -> new ImageNotFoundException("Image not found with ID: " + imageId));

            // Delete the Image from filePath system
            imageService.deleteImageByPath(image.getFilePath());

            // Delete the Image from DB
            productImageRepository.delete(image);
        } catch (ImageNotFoundException e) {
            log.error("Failed to find image to delete", e);
            throw e;
        } catch (IOException e) {
            log.error("Failed to delete image file", e);
            throw new RuntimeException("Failed to delete image file", e);
        }
    }


}

