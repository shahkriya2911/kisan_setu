package com.project.kisan_setu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Entity
@Table(name = "quality_certificates")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QualityCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long certificateId;
    private String certificateName;
    private LocalDate issuedDate;
    private String fileName;
    private String filePath;
    private String fileType;

    @OneToOne
    @JoinColumn(name = "listing_id")
    private Listing listing;
}
