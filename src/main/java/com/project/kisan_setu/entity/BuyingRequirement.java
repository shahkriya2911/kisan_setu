package com.project.kisan_setu.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "buyingrequirements")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BuyingRequirement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cropType;
    private String quantity;
    private Double minPrice;
    private Double maxPrice;
    private String qualityGrade;
    private String delieveryLocation;
    private LocalDateTime deadline;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

}
