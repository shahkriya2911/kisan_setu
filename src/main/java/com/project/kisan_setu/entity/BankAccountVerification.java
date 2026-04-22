package com.project.kisan_setu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_account_verifications")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountVerification extends Auditable{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bankId;
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String accountHolderName;
    private String upiId;
    private boolean verified = false;
    private LocalDateTime verifiedAt;
    private LocalDateTime submittedAt = LocalDateTime.now();
    private String aadhaarPath;
    private String panCardPath;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}