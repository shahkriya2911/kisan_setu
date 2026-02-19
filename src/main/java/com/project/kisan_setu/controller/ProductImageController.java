package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.ProductImageListingDto;
import com.project.kisan_setu.entity.ProductImage;
import com.project.kisan_setu.service.ProductImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/images")
public class ProductImageController {

    private final ProductImageService productImageService;
    private static final Logger logger= LoggerFactory.getLogger(ProductImageController.class);


    public ProductImageController(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(
            @RequestParam("listingId") Long listingId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isPrimary", required = false) Boolean isPrimary
    ) throws IOException {
        logger.info("Received request to upload image for listingId: {}", listingId);
        try {
            ProductImageListingDto dto = new ProductImageListingDto();
            dto.setListingId(listingId);
            dto.setFile(file);
            dto.setIsPrimary(isPrimary);

            productImageService.uploadImage(dto);
            logger.info("Image uploaded successfully for listingId: {}", listingId);
            return ResponseEntity.ok("Image uploaded successfully");
        } catch (IOException e) {
            logger.error("Error occurred while uploading image for listingId: {}", listingId, e);
            throw e;
        }
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<String> deleteImage(@PathVariable Long imageId) {
        logger.warn("Request received to delete image with ID: {}", imageId);
        productImageService.deleteImage(imageId);
        logger.warn("Image deleted successfully with ID: {}", imageId);
        return ResponseEntity.ok("Image deleted successfully");
    }
}

