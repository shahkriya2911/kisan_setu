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

@Getter //getters
@Setter //setters
@AllArgsConstructor //constructor
@NoArgsConstructor //needed by JPA
@Entity //table creation
@Table(name = "listings") //table name
public class Listing {

    @Id //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto-increment
    private Long listingId;

    //  Product Info
    private String cropName;
    private String variety;
    private String grade;
    private LocalDate harvestDate;

    //  Quantity & Pricing
    private Integer quantity;
    private String unit;
    private Double pricePerKg;
    private String purchaseType;
    private Double minimumBidIncrement;
    private Double totalBasePrice;
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

    //relationship with user
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    //relationship with images
    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL)
    private List<ProductImage> images;

    //relationship with quality certificate
    @OneToOne(mappedBy = "listing", cascade = CascadeType.ALL)
    private QualityCertificate certificate;

    //relationship with bid
    @OneToMany(mappedBy = "listing",cascade = CascadeType.ALL)
    private List<Bid> bids;

    //relationship with buying requirement
    @OneToOne(mappedBy = "listing",cascade = CascadeType.ALL)
    private List<BuyingRequirement> buyingRequirements;

    @Enumerated(EnumType.STRING)
    private AuctionStatus status;

//    @ManyToOne
//    @JoinColumn(name = "winner_id")
//    private User winner;
}
