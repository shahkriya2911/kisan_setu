package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.QualityCertificateListingDto;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.ProductImage;
import com.project.kisan_setu.entity.QualityCertificate;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.repository.ListingRepository;
import com.project.kisan_setu.repository.QualityCertificateRepository;
import com.project.kisan_setu.service.QualityCertificateService;
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

    public QualityCertificateServiceImpl(QualityCertificateRepository qualityCertificateRepository, ListingRepository listingRepository) {
        this.qualityCertificateRepository = qualityCertificateRepository;
        this.listingRepository = listingRepository;
    }
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/certificates/";

    public QualityCertificate uploadCertificate(QualityCertificateListingDto dto) throws IOException {
        MultipartFile file= dto.getFile();
        if(file == null || file.isEmpty())
        {
            throw new UserException("File is empty");
        }
        Long ListingId = dto.getListingId();
        Listing listing=listingRepository.findById(ListingId).orElseThrow(()-> new UserException("Listing not Found with id: "+ListingId));
        File directory = new File(uploadDir);
        if(!directory.exists())directory.mkdirs();

        String fileName = UUID.randomUUID()+"_"+file.getOriginalFilename();
        String filePath = uploadDir + fileName;

        //  Save file to disk
        file.transferTo(new File(filePath));
        QualityCertificate qualityCertificate=new QualityCertificate();
        qualityCertificate.setFileName(fileName);
        qualityCertificate.setIssuedDate(LocalDate.now());
        qualityCertificate.setFilePath(filePath);
        qualityCertificate.setFileType(file.getContentType());
        qualityCertificate.setCertificateName(dto.getCertificateName());
        qualityCertificate.setListing(listing);

        return qualityCertificateRepository.save(qualityCertificate);

    }
    public void deleteCertificate(Long certificateId){
        QualityCertificate certificate=qualityCertificateRepository.findById(certificateId)
                .orElseThrow(()-> new UserException("Image not Found with id:"+certificateId));

        File file = new File(certificate.getFilePath());
        if(file.exists())file.delete();

        qualityCertificateRepository.delete(certificate);

    }
}
