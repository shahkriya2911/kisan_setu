package com.project.kisan_setu.entity;

import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.enums.PurchaseType;
import com.project.kisan_setu.enums.SaleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    // Product Info
    private String cropName;
    private String variety;
    private String grade;
    private LocalDate harvestDate;
    @Enumerated(EnumType.STRING)
    private PurchaseType purchaseType; // Whole Lot Only / Partial Orders Allowed

    // Quantity
    private Integer quantity;
    private Double remainingQuantity;
    private String unit;

    // Pricing & Purchase Type
    private Double pricePerKg;
    private Double totalBasePrice;
    @Enumerated(EnumType.STRING)
    private SaleType saleType;
    private Double minimumBidIncrement;
    private LocalDateTime createdTime;// For auction
    private LocalDateTime auctionEndTime;

    // Partial order
    private Integer minimumOrderQuantity;
    private Double moqPricePerKg;


    private String state;
    private String packagingType;
    private String district;
    private String storageType;
    private String pickupMethod;

    // Auction status
    @Enumerated(EnumType.STRING)
    private AuctionStatus status;

    @Column(nullable = true)
    private String description;

    // Relationship with user
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    // Relationship with images
    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL)
    private List<ProductImage> images = new ArrayList<>();

    // Relationship with quality certificate
    @OneToOne(mappedBy = "listing", cascade = CascadeType.ALL)
    private QualityCertificate certificate;

    // Relationship with bids
    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL)
    private List<Bid> bids = new ArrayList<>();

    // Relationship with buying requirements
    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL)
    private List<BuyingRequirement> buyingRequirements = new ArrayList<>();



    // Optional winner
    // @ManyToOne
    // @JoinColumn(name = "winner_id")
    // private User winner;
}