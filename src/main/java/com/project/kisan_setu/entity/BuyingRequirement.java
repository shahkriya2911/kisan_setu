package com.project.kisan_setu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity //table creation
@Table(name = "buying_requirements") //table name
@Getter //getters
@Setter //setters
@NoArgsConstructor //needed by JPA
@AllArgsConstructor //constructor
public class BuyingRequirement {

    @Id //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto-increment
    private Long requirementId;

    //buying requirement info
    private String cropType;
    private Double quantityRequired;
    private Double minPrice;
    private Double maxPrice;
    private String qualityGrade;
    private String deliveryLocation;
    private LocalDate deadline;
    private String additionalNotes;
    private LocalDateTime createdAt;

    //relationship with listing
    @ManyToOne
    @JoinColumn(name = "listing_id")
    private Listing listing;

    //relationship with user
    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;
}