package com.project.kisan_setu.entity;

import com.project.kisan_setu.embedded.ListingCertificate;
import com.project.kisan_setu.embedded.ListingImage;
import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.enums.PurchaseType;
import com.project.kisan_setu.enums.SaleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
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
    private BigDecimal quantity;
    private BigDecimal remainingQuantity;
    private String unit;

    // Pricing & Purchase Type
    private BigDecimal pricePerKg;
    private BigDecimal totalBasePrice;
    @Enumerated(EnumType.STRING)
    private SaleType saleType;
    private BigDecimal minimumBidIncrement;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime postedOn;

    @PrePersist
    protected void onCreate() {
        if(createdAt == null){
        this.createdAt = LocalDateTime.now();}
        if(postedOn == null){
            this.postedOn = LocalDateTime.now();
        }
    }
    private LocalDateTime auctionEndTime;

    // Partial order
    private BigDecimal minimumOrderQuantity;
    private BigDecimal moqPricePerKg;


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
    private boolean oneDayNotified = false;
    private boolean thirtyMinuteNotified = false;

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