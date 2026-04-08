package com.project.kisan_setu.entity;

import com.project.kisan_setu.enums.DisputeStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "dispute")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dispute extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long disputeId;

    private String disputeCode;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    private String issueType;

    @Enumerated(EnumType.STRING)
    private DisputeStatus status;

    private String description;

    private Boolean evidenceRequested = false;
    private Boolean evidenceSubmitted = false;


}
