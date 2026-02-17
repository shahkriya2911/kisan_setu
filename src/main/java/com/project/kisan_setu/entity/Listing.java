package com.project.kisan_setu.entity;

import com.project.kisan_setu.enums.SaleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

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

    // 🔹 Product Info
    private String cropName;
    private String variety;
    private String grade;
    private String harvestDate;

    // 🔹 Quantity & Pricing
    private Integer quantity;
    private String unit;
    private Double basePrice;
    private String purchaseType;
    private Double minimumBidIncrement;
    @Enumerated(EnumType.STRING)
    private SaleType saleType;  // FIXED or AUCTION
    private LocalDateTime auctionEndDate;

    // 🔹 Quality & Location
    private String moisture;
    private String state;
    private String packagingType;
    private String district;
    private String storageType;
    private String pickupMethod;
    // 🔹 Seller
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    // 🔹 Images
    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL)
    private List<ListingImage> images;

    // 🔹 Certificate
    @OneToOne(mappedBy = "listing", cascade = CascadeType.ALL)
    private QualityCertificate certificate;
}
