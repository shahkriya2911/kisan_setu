package com.project.kisan_setu.controller;

import com.project.kisan_setu.dto.QualityCertificateListingDto;
import com.project.kisan_setu.entity.QualityCertificate;
import com.project.kisan_setu.service.QualityCertificateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/certificates")
public class QualityCertificateController {
    private final QualityCertificateService qualityCertificateService;

    public QualityCertificateController(QualityCertificateService qualityCertificateService) {
        this.qualityCertificateService = qualityCertificateService;
    }

    @PostMapping
    public ResponseEntity<QualityCertificate> uploadCerificate(@RequestParam("listingId")Long listingId,
                                                               @RequestParam("certificateName") String certificateName,
                                                               @RequestParam("file")MultipartFile file) throws IOException {
        QualityCertificateListingDto dto = new QualityCertificateListingDto();
        dto.setListingId(listingId);
        dto.setCertificateName(certificateName);
        dto.setFile(file);

        QualityCertificate savedCertificate = qualityCertificateService.uploadCertificate(dto);

        return ResponseEntity.ok(savedCertificate);
    }
    @DeleteMapping("/{certificateId}")
    public ResponseEntity<String> deleteCertificate(@PathVariable Long certificateId) {
        qualityCertificateService.deleteImage(certificateId);
        return ResponseEntity.ok("Certificate deleted successfully");
    }
}
