package com.project.kisan_setu.entity;
import com.project.kisan_setu.enums.EscrowStatus;
import com.project.kisan_setu.enums.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name="buyer_id")
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    @ManyToOne
    @JoinColumn(name="listing_id")
    private Listing listing;

    private BigDecimal quantity;

    private BigDecimal pricePerKg;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal amount;

    @OneToOne
    @JoinColumn(name = "accepted_bid_id")
    private Bid acceptBid;

    private LocalDateTime confirmationDeadline;

    @OneToMany(mappedBy = "order")
    private List<Notification> notifications = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private EscrowStatus escrowStatus;

    private String deliveryOtp;

    private LocalDateTime otpGeneratedAt;

    @Column(nullable = false)
    private boolean otpVerified = false;

    private Integer otpAttempts = 0;

    private LocalDateTime completedAt;

}
