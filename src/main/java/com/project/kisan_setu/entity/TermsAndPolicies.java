package com.project.kisan_setu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "terms_and_policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TermsAndPolicies {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long termsAndPoliciesId;
    @Column(columnDefinition = "TEXT")
    private String termsOfService;
    @Column(columnDefinition = "TEXT")
    private String privacyPolicy;
    @Column(columnDefinition = "TEXT")
    private String cookiePolicy;
    @Column(columnDefinition = "TEXT")
    private String refundPolicy;
    @Column(columnDefinition = "TEXT")
    private String communityGuidelines;
}
