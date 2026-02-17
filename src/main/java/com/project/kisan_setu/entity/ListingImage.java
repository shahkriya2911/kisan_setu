package com.project.kisan_setu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "listing_images")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ListingImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    private String fileName;
    private String filePath;
    private String fileType;
    private Boolean isPrimary; // for cover image

    @ManyToOne
    @JoinColumn(name = "listing_id")
    private Listing listing;
}