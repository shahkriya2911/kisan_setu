package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.QualityCertificateListingDto;
import com.project.kisan_setu.entity.QualityCertificate;

import java.io.IOException;

public interface QualityCertificateService {
    public QualityCertificate uploadCertificate(QualityCertificateListingDto dto)throws IOException;
    void deleteCertificate(Long certificateId);
}
