package com.project.kisan_setu.entity;

import com.project.kisan_setu.enums.BidStatus;
import jakarta.annotation.Resource;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bids")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bid extends Auditable{

    @Id //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto-increment
    private Long bidId;

    //bid info
    private BigDecimal buyerAmount;
    private LocalDateTime bidTime;

    @Enumerated(EnumType.STRING)
    private BidStatus bidStatus =  BidStatus.PENDING;

    private BigDecimal amountPerKg;
    private LocalDateTime acceptedTime;
    private String buyerResponse; // or enum (ACCEPT / REJECT)
    private boolean buyerFlagProcessed = false;

    //relationship with listing
    @ManyToOne
    @JoinColumn(name = "listing_id")
    private Listing listing;

    //relationship with buyer
    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @OneToMany(mappedBy = "bid")
    private List<Notification> notifications = new ArrayList<>();


}