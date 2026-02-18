package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.ProductImageListingDto;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.ProductImage;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.repository.ProductImageRepository;
import com.project.kisan_setu.repository.ListingRepository;
import com.project.kisan_setu.service.ProductImageService;
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

    public ProductImageServiceImpl(ProductImageRepository productImageRepository, ListingRepository listingRepository) {
        this.productImageRepository = productImageRepository;
        this.listingRepository = listingRepository;
    }
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/productimage/";

    @Override
    public ProductImage uploadImage(ProductImageListingDto dto) throws IOException {
        MultipartFile file = dto.getFile();
        if (file == null || file.isEmpty()) {
            throw new UserException("File is empty");
        }
        Long LisingId = dto.getListingId();
        Listing listing=listingRepository.findById(LisingId).orElseThrow(()-> new UserException("Listing Not Found with id :" +LisingId));
        File directory = new File(uploadDir);
        if(!directory.exists())directory.mkdirs();

        String fileName = UUID.randomUUID()+"-"+file.getOriginalFilename();
        String filePath = uploadDir + fileName;

        //  Save file to disk
        file.transferTo(new File(filePath));
        ProductImage productImage = new ProductImage();
        productImage.setFileName(fileName);
        productImage.setFilePath(filePath);
        productImage.setFileType(file.getContentType());
        productImage.setIssuedDate(LocalDate.now());
        productImage.setIsPrimary(dto.getIsPrimary());
        productImage.setListing(listing);

        return productImageRepository.save(productImage);

    }

    public void deleteImage(Long imageId){
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new UserException("Image not found with id " + imageId));

        // Delete file from disk
        File file = new File(image.getFilePath());
        if (file.exists()) file.delete();

        // Delete record from DB
        productImageRepository.delete(image);

    }




}
