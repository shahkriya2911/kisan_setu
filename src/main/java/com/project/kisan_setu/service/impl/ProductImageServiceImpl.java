package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.ProductImageListingDto;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.ProductImage;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.repository.ProductImageRepository;
import com.project.kisan_setu.repository.ListingRepository;
import com.project.kisan_setu.service.ProductImageService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;


@Service
public class ProductImageServiceImpl implements ProductImageService {
    // Folder to store uploaded images

    private final ProductImageRepository productImageRepository;
    private final ListingRepository listingRepository;
    private static final Logger logger= LoggerFactory.getLogger(ProductImageServiceImpl.class);

    public ProductImageServiceImpl(ProductImageRepository productImageRepository, ListingRepository listingRepository) {
        this.productImageRepository = productImageRepository;
        this.listingRepository = listingRepository;
    }
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/productimage/";

    @Override
    public ProductImage uploadImage(ProductImageListingDto dto) throws IOException {
        MultipartFile file = dto.getFile();
        if (file == null || file.isEmpty()) {
            logger.warn("Attempted to upload empty file for listingId: {}", dto.getListingId());
            throw new UserException("File is empty");
        }
        Long LisingId = dto.getListingId();
        Listing listing=listingRepository.findById(LisingId).orElseThrow(()->{logger.error("Listing not found with id: {}",LisingId);
        return new UserException("Listing Not Found with id :" +LisingId); } );

        File directory = new File(uploadDir);
        if(!directory.exists())directory.mkdirs();
        logger.info("Created upload directory: {}", uploadDir);

        String fileName = UUID.randomUUID()+"-"+file.getOriginalFilename();
        String filePath = uploadDir + fileName;

        //  Save file to disk
        file.transferTo(new File(filePath));
        logger.info("File saved: {} for listingId: {}", fileName, LisingId);
        ProductImage productImage = new ProductImage();
        productImage.setFileName(fileName);
        productImage.setFilePath(filePath);
        productImage.setFileType(file.getContentType());
        productImage.setIssuedDate(LocalDate.now());
        productImage.setIsPrimary(dto.getIsPrimary());
        productImage.setListing(listing);

        ProductImage image = productImageRepository.save(productImage);
        logger.info("Image Stored in DB with id : {}",image.getImageId());

        return image;

    }

    public void deleteImage(Long imageId){
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> {
                    logger.error("Attempted to delete non-existing image with id: {}", imageId);
                    return new UserException("Image not found with id " + imageId);
                });

        // Delete file from disk
        File file = new File(image.getFilePath());
        if (file.exists()) {
            if (file.delete()) {
                logger.info("Deleted file from disk: {}", image.getFilePath());
            } else {
                logger.warn("Failed to delete file from disk: {}", image.getFilePath());
            }
        }

        // Delete record from DB
        productImageRepository.delete(image);
        logger.info("Deleted ProductImage record from DB with id: {}", imageId);
    }

}





