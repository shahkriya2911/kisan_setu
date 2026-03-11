package com.project.kisan_setu.entity;
import com.project.kisan_setu.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String fullName;
    private String email;
    @Column(nullable = false,unique = true)
    private String mobileNumber;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    private String dateOfBirth;
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String upiId;

    private BigDecimal farmSize;
    private String primaryCrops;
    private String farmLocation;
    private Integer yearsOfExperience;
    @Column(name = "profile_photo")
    private String profilePhoto;



    @OneToMany(mappedBy = "seller")
    private List<Listing> listings = new ArrayList<>();

    @OneToMany(mappedBy = "buyer")
    private List<BuyingRequirement> buyingRequirements = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private AadhaarVerification aadhaarVerification;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private BankAccountVerification bankAccountVerification;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private MobileVerification mobileVerification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;  // default everyone is USER



}
