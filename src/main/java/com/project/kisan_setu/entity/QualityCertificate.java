package com.project.kisan_setu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Entity //table creation
@Table(name = "quality_certificates") //table name
@AllArgsConstructor //constructor
@NoArgsConstructor //needed by JPA
@Getter //getters
@Setter //setters
public class QualityCertificate {

    @Id //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto-increment
    private Long certificateId;

    //quality certificate info
    private String certificateName;
    private LocalDate issuedDate;
    private String fileName;
    private String filePath;
    private String fileType;

    //relationship with listing
    @OneToOne
    @JoinColumn(name = "listing_id")
    private Listing listing;
}
