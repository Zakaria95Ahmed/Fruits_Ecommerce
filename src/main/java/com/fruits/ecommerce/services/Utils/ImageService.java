package com.fruits.ecommerce.services.Utils;

import com.fruits.ecommerce.exceptions.exceptionsDomain.products.InvalidImageException;
import com.fruits.ecommerce.models.entities.ProductImage;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
@Slf4j
@Service
public class ImageService {

    // if we use storing-image directly with project-files
//    @Value("${image.upload-dir}")
//    private String imageUploadDir;

    @Value("${file.upload-dir}")
    private String fileUploadDir;

    @Value("${image.allowed-mime-types}")
    private String allowedMimeTypesConfig;

    @Value("${image.max-file-size}")
    private long maxFileSize;
    private List<String> allowedMimeTypes;

    @PostConstruct
    public void init() {
        allowedMimeTypes = Arrays.asList(allowedMimeTypesConfig.split(","));
    }

    public ProductImage saveImage(MultipartFile image) throws IOException, InvalidImageException {
        // validate Image
        validateImage(image);
        // generate Unique FileName
        String filename = generateUniqueFileName(image);
        // Determine File path
        Path fileStorageLocation = Paths.get(fileUploadDir).toAbsolutePath().normalize();
        Path targetLocation = fileStorageLocation.resolve(filename);
        Files.createDirectories(targetLocation.getParent());
        // Save the image in the file system
        Files.copy(image.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        // Create an object from ProductImage
        ProductImage productImage = new ProductImage();
        // Full path on the server
        productImage.setFilePath(targetLocation.toString());
        // Client-accessible URL
        productImage.setImageUrl("/images/" + filename);
        return productImage;

    }

    private void validateImage(MultipartFile image) throws InvalidImageException {
        if (image.getSize() > maxFileSize) {
            throw new InvalidImageException("Image size exceeds the allowed limit.");
        }
        String contentType = image.getContentType();
        if (!allowedMimeTypes.contains(contentType)) {
            throw new InvalidImageException("Image format is not supported. Allowed formats: " + allowedMimeTypesConfig);
        }
    }

    private String generateUniqueFileName(MultipartFile image) {
        String extension = FilenameUtils.getExtension(image.getOriginalFilename());
        return UUID.randomUUID() + "." + extension;
    }

    // load Image As Resource
    public Resource loadImageAsResource(String imageUrl) throws MalformedURLException, FileNotFoundException {
        // Extract the filename from imageUrl
        String filename = Paths.get(imageUrl).getFileName().toString();
        Path filePath = Paths.get(fileUploadDir).resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists()) {
            return resource;
        } else {
            throw new FileNotFoundException("Image not found at path: " + filename);
        }
    }
    public void deleteImage(String imageUrl) throws IOException {
        Path filePath = Paths.get(fileUploadDir, imageUrl);
        Files.deleteIfExists(filePath);
    }
    public void deleteImageByPath(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.deleteIfExists(path);
    }
    public Resource loadImageByPath(String filePath) throws MalformedURLException, FileNotFoundException {
        Path path = Paths.get(filePath).normalize();
        Resource resource = new UrlResource(path.toUri());
        if (resource.exists()) {
            return resource;
        } else {
            throw new FileNotFoundException("Image not found at path: " + filePath);
        }
    }
}



