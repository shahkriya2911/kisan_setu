package com.project.kisan_setu.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_otp")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Long buyerId;

    private String otp;

    private LocalDateTime expiryTime;

    @Column(nullable = false)
    private boolean verified = false;

    private int attempts = 0;
    private LocalDateTime createdAt;

    @PrePersist
    public void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }
}