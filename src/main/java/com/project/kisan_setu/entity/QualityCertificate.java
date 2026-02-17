package com.project.kisan_setu.entity;

import jakarta.persistence.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Entity
@Table(name = "quality_certificates")
public class QualityCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long certificateId;
    private MultipartFile qualityCertificate;
    private String certificateName;
    private LocalDate issuedDate;

    @OneToOne
    @JoinColumn(name = "listing_id")
    private Listing listing;
}
