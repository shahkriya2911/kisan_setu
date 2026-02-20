package com.project.kisan_setu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "buying_requirements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuyingRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requirementId;

    private String cropType;
    private Double quantityRequired;
    private Double minPrice;
    private Double maxPrice;
    private String qualityGrade;
    private String deliveryLocation;
    private LocalDate deadline;
    private String additionalNotes;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    private LocalDateTime createdAt;
}