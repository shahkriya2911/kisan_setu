package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.QualityCertificateListingDto;
import com.project.kisan_setu.entity.QualityCertificate;
import com.project.kisan_setu.service.QualityCertificateService;
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

        QualityCertificateListingDto dto = new QualityCertificateListingDto();
        dto.setListingId(listingId);
        dto.setCertificateName(certificateName);
        dto.setIssuedDate(issuedDate);
        dto.setFile(file);

        qualityCertificateService.uploadCertificate(dto);

        return ResponseEntity.ok("Certificate uploaded successfully");
    }

    @DeleteMapping("/{certificateId}")
    public ResponseEntity<String> deleteCertificate(@PathVariable Long certificateId) {
        qualityCertificateService.deleteCertificate(certificateId); // rename service method
        return ResponseEntity.ok("Certificate deleted successfully");
    }
}

