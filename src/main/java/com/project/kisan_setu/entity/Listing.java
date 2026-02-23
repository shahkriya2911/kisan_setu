package com.project.kisan_setu.entity;

import com.project.kisan_setu.enums.AuctionStatus;
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

    // Quantity
    private Integer quantity;
    private String unit;

    // Purchase Type
    private String purchaseType;
    // Whole Lot Only / Partial Orders Allowed

    // AUCTION (Whole Lot)
    private Double pricePerKg;
    private Double totalBasePrice;
    private Double minimumBidIncrement;
    private LocalDateTime auctionEndTime;

    @Enumerated(EnumType.STRING)
    private SaleType saleType;

    // PARTIAL ORDER (Fixed)
    private Integer minimumOrderQuantity;
    private Double moqPricePerKg;



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

    @Enumerated(EnumType.STRING)
    private AuctionStatus status;

//    @ManyToOne
//    @JoinColumn(name = "winner_id")
//    private User winner;
}
