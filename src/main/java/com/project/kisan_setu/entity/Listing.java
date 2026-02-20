package com.project.kisan_setu.entity;

import com.project.kisan_setu.enums.SaleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "listings")
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long listingId;

    //  Product Info
    private String cropName;
    private String variety;
    private String grade;
    private LocalDate harvestDate;

    //  Quantity & Pricing
    private Integer quantity;
    private String unit;
    private Double basePrice;
    private String purchaseType;
    private Double minimumBidIncrement;
    @Enumerated(EnumType.STRING)
    private SaleType saleType;  // FIXED or AUCTION
    private LocalDateTime auctionEndTime;

    //  Quality & Location
    private String moisture;
    private String state;
    private String packagingType;
    private String district;
    private String storageType;
    private String pickupMethod;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;


    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL)
    private List<ProductImage> images;


    @OneToOne(mappedBy = "listing", cascade = CascadeType.ALL)
    private QualityCertificate certificate;
}
