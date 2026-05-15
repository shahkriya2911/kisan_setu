package com.project.kisan_setu.entity;
import com.project.kisan_setu.enums.ReportStatus;
import com.project.kisan_setu.enums.ReportedBy;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "report",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"order_id", "reported_by"})
        }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Report extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    private String reason;

    @Column(nullable = false, length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Enumerated(EnumType.STRING)
    @Column(name = "reported_by", nullable = false)
    private ReportedBy reportedBy;

    @Column(nullable = false)
    private Boolean isBuyerReported = false;

    @Column(nullable = false)
    private Boolean isSellerReported = false;

}
