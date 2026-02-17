package com.project.kisan_setu.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "listing_images")
public class ListingImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    private String imageUrl;

    private Boolean isPrimary; // for cover image

    @ManyToOne
    @JoinColumn(name = "listing_id")
    private Listing listing;
}