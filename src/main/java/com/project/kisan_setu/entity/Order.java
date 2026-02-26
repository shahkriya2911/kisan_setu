package com.project.kisan_setu.entity;

import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name="buyer_id")
    private User buyer;

    @ManyToOne
    @JoinColumn(name="listing_id")
    private Listing listing;

    private Integer quantity;

    private Double pricePerKg;

    private Double totalBasePrice;

    private LocalDateTime orderTime;
}