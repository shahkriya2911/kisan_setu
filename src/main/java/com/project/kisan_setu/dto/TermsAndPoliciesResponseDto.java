package com.project.kisan_setu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TermsAndPoliciesResponseDto {
    private Long termsAndPoliciesId;
    private String termsOfService;
    private String privacyPolicy;
    private String cookiePolicy;
    private String refundPolicy;
    private String communityGuidelines;
}
