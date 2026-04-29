package com.project.kisan_setu.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_otp")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderOtp extends Auditable{

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
}