package com.project.kisan_setu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "bids")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bid {

    @Id //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto-increment
    private Long bidId;

    //bid info
    private Double bidAmount;
    private LocalDateTime bidTime;

    private LocalDateTime createdAt = LocalDateTime.now();

    //relationship with listing
    @ManyToOne
    @JoinColumn(name = "listing_id")
    private Listing listing;

    //relationship with buyer
    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;
}