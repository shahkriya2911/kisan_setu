package com.project.kisan_setu.entity;

import com.project.kisan_setu.enums.InquiryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "buyerInquires")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyerInquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inquiryId;

    @ManyToOne
    private Listing listing;

    @ManyToOne
    private User buyer;

    private BigDecimal quantityRequested;
    private LocalDateTime respondedAt;
    private LocalDateTime inquiryTime;
    @Enumerated(EnumType.STRING)
    private InquiryStatus status; // PENDING, ACCEPTED, REJECTED
    private LocalDateTime createdAt;
    @PrePersist
    public void prePersist() {
        if (inquiryTime == null) {
            inquiryTime = LocalDateTime.now();
        }
        if (status == null) {
            status = InquiryStatus.PENDING;
        }
    }


}
