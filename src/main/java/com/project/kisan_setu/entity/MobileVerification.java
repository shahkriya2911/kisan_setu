package com.project.kisan_setu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "mobile_verifications")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MobileVerification extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String mobileNumber;
    private String otp;
    private boolean verified = false;
    private LocalDateTime otpSentAt;
    private LocalDateTime verifiedAt;
    private LocalDateTime expiresAt;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}