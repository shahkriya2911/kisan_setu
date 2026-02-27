package com.project.kisan_setu.entity;

import com.project.kisan_setu.embedded.ListingCertificate;
import com.project.kisan_setu.embedded.ListingImage;
import com.project.kisan_setu.enums.ListingStatus;
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
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
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
    private ListingStatus status;

    @Column(nullable = true)
    private String description;

    // Relationship with user
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    // images
    @ElementCollection
    @CollectionTable(name = "listing_images", joinColumns = @JoinColumn(name = "listing_id"))
    private List<ListingImage> images = new ArrayList<>();

    //quality certificate info
    @Embedded
    private ListingCertificate certificate;


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