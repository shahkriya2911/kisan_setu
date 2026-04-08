package com.project.kisan_setu.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "fruadReport")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FraudReport extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reportedBy;

    private Long reportedUser;

    @ManyToOne
    @JoinColumn(name = "fraud_type_id")
    private FraudType fraudType;

    private String description;

    private String status;


}
