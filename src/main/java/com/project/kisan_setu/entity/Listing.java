package com.project.kisan_setu.entity;

import com.project.kisan_setu.enums.SaleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
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
    private String description;

    // 🔹 Quantity & Pricing
    private Integer quantity;
    private Double pricePerUnit;
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    private SaleType saleType;  // FIXED or AUCTION

    // 🔹 Auction Timing (nullable if FIXED)
    private LocalDateTime biddingStartTime;
    private LocalDateTime biddingEndTime;

    // 🔹 Quality & Location
    private String grade;
    private String storageCondition;
    private String location;

    // 🔹 Status Management
    private Boolean isDraft;
    private Boolean isPublished;
    private Boolean isActive;

    private LocalDateTime createdAt;

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
