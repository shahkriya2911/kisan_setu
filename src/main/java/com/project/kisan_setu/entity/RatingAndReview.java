package com.project.kisan_setu.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        name = "ratingsAndReview",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"order_id", "isBuyerReview"})
        }
)
@Data
public class RatingAndReview extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false, length = 1000)
    private String review;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(nullable = false)
    private Boolean isBuyerReview;
}