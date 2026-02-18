package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.ProductImageListingDto;
import com.project.kisan_setu.entity.ProductImage;
import com.project.kisan_setu.service.ProductImageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/images")
public class ProductImageController {

    private final ProductImageService productImageService;

    public ProductImageController(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(
            @RequestParam("listingId") Long listingId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isPrimary", required = false) Boolean isPrimary
    ) throws IOException {

        ProductImageListingDto dto = new ProductImageListingDto();
        dto.setListingId(listingId);
        dto.setFile(file);
        dto.setIsPrimary(isPrimary);

        productImageService.uploadImage(dto);

        return ResponseEntity.ok("Image uploaded successfully");
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<String> deleteImage(@PathVariable Long imageId) {
        productImageService.deleteImage(imageId);
        return ResponseEntity.ok("Image deleted successfully");
    }
}

