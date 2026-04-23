package com.project.kisan_setu.entity;
import com.project.kisan_setu.embedded.ListingCertificate;
import com.project.kisan_setu.embedded.ListingImage;
import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.enums.PurchaseType;
import com.project.kisan_setu.enums.SaleType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
public class Listing extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long listingId;

    // Product Info
    private String variety;
    private String grade;
    private LocalDate harvestDate;
    @Enumerated(EnumType.STRING)
    private PurchaseType purchaseType;

    // Quantity
    private BigDecimal quantity;

    // Pricing & Purchase Type
    private BigDecimal pricePerKg;
    private BigDecimal totalBasePrice;
    @Enumerated(EnumType.STRING)
    private SaleType saleType;
    private BigDecimal minimumBidIncrement;
    private BigDecimal maximumBidIncrement;
    private LocalDateTime postedOn;

    @PrePersist
    protected void onCreate() {
        if(postedOn == null){
            this.postedOn = LocalDateTime.now();
        }
    }
    private LocalDateTime auctionEndTime;

    // Partial order
    private BigDecimal minimumOrderQuantity;
    private BigDecimal moqPricePerKg;
    private String pickupMethod;

    // Auction status
    @Enumerated(EnumType.STRING)
    private AuctionStatus status;

    @Column(nullable = true)
    private String description;
    private boolean oneDayNotified = false;
    private boolean thirtyMinuteNotified = false;
    @Column(nullable = false)
    private boolean sellerFlagProcessed = false;
    private Boolean isSold;


    // Relationship with user
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    @ElementCollection
    @CollectionTable(
            name = "listing_images",
            joinColumns = @JoinColumn(name = "listing_id")
    )
    @AttributeOverrides({
            @AttributeOverride(name = "fileName", column = @Column(name = "file_name")),
            @AttributeOverride(name = "filePath", column = @Column(name = "file_path")),
            @AttributeOverride(name = "fileType", column = @Column(name = "file_type")),
            @AttributeOverride(name = "issuedDate", column = @Column(name = "issued_date")),
            @AttributeOverride(name = "isPrimary", column = @Column(name = "is_primary"))
    })
    private List<ListingImage> images = new ArrayList<>();

    //quality certificate info
    @Embedded
    private ListingCertificate certificate;


    // Relationship with bids
    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL)
    private List<Bid> bids = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "crop_id")
    private CropMaster crop;

    @ManyToOne
    @JoinColumn(name = "packaging_id")
    private PackagingMaster packaging;

    @ManyToOne
    @JoinColumn(name = "storage_id")
    private StorageMaster storage;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private UnitMaster unit;

    @ManyToOne()
    @JoinColumn(name = "state_id")
    private StateMaster state;

    @ManyToOne()
    @JoinColumn(name = "district_id")
    private DistrictMaster district;

    @OneToMany(mappedBy = "listing")
    private List<Notification> notifications = new ArrayList<>();
    private BigDecimal topBid;
    private Boolean bidAccepted = false;




    // Optional winner
    // @ManyToOne
    // @JoinColumn(name = "winner_id")
    // private User winner;
}
