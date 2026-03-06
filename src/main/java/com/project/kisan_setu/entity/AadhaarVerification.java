package com.project.kisan_setu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "aadhaar_verifications")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AadhaarVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aadhaarId;
    private String aadhaarNumber;
    private String nameOnAadhaar;
    private String dateOfBirth;
    private String address;
    private String aadhaarImagePath;
    private boolean verified = false;
    private LocalDateTime verifiedAt;
    private LocalDateTime submittedAt = LocalDateTime.now();

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;


}
