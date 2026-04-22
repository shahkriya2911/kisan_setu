package com.project.kisan_setu.entity;
import com.project.kisan_setu.enums.RequirementStatus;
import com.project.kisan_setu.enums.Urgency;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "buying_requirements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuyingRequirement {

    @Id //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requirementId;

    //buying requirement info
    private String variety;
    private String grade;
    private BigDecimal quantityRequired;
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

    @ManyToOne
    @JoinColumn(name = "crop_id")
    private CropMaster crop;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private UnitMaster unit;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;


}