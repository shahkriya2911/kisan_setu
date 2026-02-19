package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.QualityCertificateListingDto;
import com.project.kisan_setu.entity.QualityCertificate;
import com.project.kisan_setu.service.QualityCertificateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/certificates")
public class QualityCertificateController {

    private final QualityCertificateService qualityCertificateService;
    private static final Logger logger= LoggerFactory.getLogger(QualityCertificateController.class);

    public QualityCertificateController(QualityCertificateService qualityCertificateService) {
        this.qualityCertificateService = qualityCertificateService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadCertificate(
            @RequestParam("listingId") Long listingId,
            @RequestParam("certificateName") String certificateName,
            @RequestParam("issuedDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issuedDate,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        logger.info("Received request to upload certificate for listingId: {}", listingId);
        QualityCertificateListingDto dto = new QualityCertificateListingDto();
        dto.setListingId(listingId);
        dto.setCertificateName(certificateName);
        dto.setIssuedDate(issuedDate);
        dto.setFile(file);

        qualityCertificateService.uploadCertificate(dto);
        logger.info("Certificate uploaded successfully for listingId: {}", listingId);
        return ResponseEntity.ok("Certificate uploaded successfully");
    }

    @DeleteMapping("/{certificateId}")
    public ResponseEntity<String> deleteCertificate(@PathVariable Long certificateId) {
        logger.warn("Received request to delete certificate with ID: {}", certificateId);

        qualityCertificateService.deleteCertificate(certificateId);

        logger.warn("Certificate deleted successfully with ID: {}", certificateId);

        return ResponseEntity.ok("Certificate deleted successfully");
    }
}

