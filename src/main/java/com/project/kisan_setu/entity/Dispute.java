package com.project.kisan_setu.entity;
import com.project.kisan_setu.enums.DisputeStatus;
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
