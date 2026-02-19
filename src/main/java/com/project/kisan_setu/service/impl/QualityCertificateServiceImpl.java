package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.QualityCertificateListingDto;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.ProductImage;
import com.project.kisan_setu.entity.QualityCertificate;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.repository.ListingRepository;
import com.project.kisan_setu.repository.QualityCertificateRepository;
import com.project.kisan_setu.service.QualityCertificateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.cert.Certificate;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class QualityCertificateServiceImpl implements QualityCertificateService {
    private final QualityCertificateRepository qualityCertificateRepository;
    private final ListingRepository listingRepository;
    private static final Logger logger = LoggerFactory.getLogger(QualityCertificateServiceImpl.class);

    public QualityCertificateServiceImpl(QualityCertificateRepository qualityCertificateRepository, ListingRepository listingRepository) {
        this.qualityCertificateRepository = qualityCertificateRepository;
        this.listingRepository = listingRepository;
    }
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/certificates/";

    public QualityCertificate uploadCertificate(QualityCertificateListingDto dto) throws IOException {
        logger.info("Starting certificate upload process");
        MultipartFile file= dto.getFile();
        if(file == null || file.isEmpty())
        {
            logger.error("File upload failed: File is empty");
            throw new UserException("File is empty");
        }
        Long ListingId = dto.getListingId();
        logger.debug("Fetching listing with ID: {}", ListingId);
        Listing listing=listingRepository.findById(ListingId).orElseThrow(()->{logger.error("Listing not found with ID: {}", ListingId);
            return new UserException("Listing not Found with id: "+ListingId);
        });
        File directory = new File(uploadDir);
        if(!directory.exists())directory.mkdirs();
        logger.info("Upload directory created at: {}", uploadDir);

        String fileName = UUID.randomUUID()+"_"+file.getOriginalFilename();
        String filePath = uploadDir + fileName;
        logger.debug("Saving file to path: {}", filePath);

        //  Save file to disk
        file.transferTo(new File(filePath));
        logger.info("File saved: {} for listingId: {}", fileName, ListingId);
        QualityCertificate qualityCertificate=new QualityCertificate();
        qualityCertificate.setFileName(fileName);
        qualityCertificate.setIssuedDate(LocalDate.now());
        qualityCertificate.setFilePath(filePath);
        qualityCertificate.setFileType(file.getContentType());
        qualityCertificate.setCertificateName(dto.getCertificateName());
        qualityCertificate.setListing(listing);

        QualityCertificate Certificate = qualityCertificateRepository.save(qualityCertificate);

        logger.info("Certificate uploaded successfully with ID: {}", Certificate.getCertificateId());

        return Certificate;

    }
    public void deleteCertificate(Long certificateId){
        logger.info("Attempting to delete certificate with ID: {}", certificateId);
        QualityCertificate certificate=qualityCertificateRepository.findById(certificateId)
                .orElseThrow(()-> new UserException("Image not Found with id:"+certificateId));

        File file = new File(certificate.getFilePath());
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                logger.info("File deleted from disk: {}", certificate.getFilePath());
            } else {
                logger.warn("File could not be deleted: {}", certificate.getFilePath());
            }
        }

        qualityCertificateRepository.delete(certificate);
        logger.info("Certificate deleted successfully with ID: {}", certificateId);

    }
}
