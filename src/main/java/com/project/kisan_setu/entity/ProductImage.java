package com.project.kisan_setu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "listing_images")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;
    private String fileName;
    private String filePath;
    private String fileType;
    private LocalDate issuedDate;
    private Boolean isPrimary; // for cover image

    @ManyToOne
    @JoinColumn(name = "listing_id")
    private Listing listing;
}