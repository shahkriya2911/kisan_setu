package com.project.kisan_setu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity //table creation
@Table(name = "listing_images") //table name
@AllArgsConstructor //constructor
@NoArgsConstructor //needed by JPA
@Getter //getters
@Setter //setters
public class ProductImage {

    @Id //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto-increment

    //image info
    private Long imageId;
    private String fileName;
    private String filePath;
    private String fileType;
    private LocalDate issuedDate;

    @ManyToOne
    @JoinColumn(name = "listing_id")
    private Listing listing;
    private Boolean isPrimary; // for cover image

}