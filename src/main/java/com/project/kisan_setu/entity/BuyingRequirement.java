package com.project.kisan_setu.entity;

import com.project.kisan_setu.enums.RequirementStatus;
import com.project.kisan_setu.enums.Urgency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity //table creation
@Table(name = "buying_requirements") //table name
@Data
@NoArgsConstructor //needed by JPA
@AllArgsConstructor //constructor
public class BuyingRequirement {

    @Id //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto-increment
    private Long requirementId;

    //buying requirement info
    private String cropName;
    private String variety;
    private String grade;
    private BigDecimal quantityRequired;
    private String unit;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String deliveryAddress;
    private LocalDate deadline;
    @Enumerated(EnumType.STRING)
    private Urgency urgency;
    private String additionalNote;
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private RequirementStatus requirementStatus;

    //relationship with user
    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @ManyToOne()
    @JoinColumn(name = "state_id")
    private StateMaster state;

    @ManyToOne()
    @JoinColumn(name = "district_id")
    private DistrictMaster district;
}