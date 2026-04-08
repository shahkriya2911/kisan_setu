package com.project.kisan_setu.mapper;
import com.project.kisan_setu.dto.ResponseDto.TermsAndPoliciesResponseDto;
import com.project.kisan_setu.entity.TermsAndPolicies;

public class TermsAndPoliciesMapper {
    public static TermsAndPoliciesResponseDto toDto(TermsAndPolicies policies){
        TermsAndPoliciesResponseDto termsAndPoliciesResponseDto = new TermsAndPoliciesResponseDto();
        termsAndPoliciesResponseDto.setTermsAndPoliciesId(policies.getTermsAndPoliciesId());
        termsAndPoliciesResponseDto.setPrivacyPolicy(policies.getPrivacyPolicy());
        termsAndPoliciesResponseDto.setTermsOfService(policies.getTermsOfService());
        termsAndPoliciesResponseDto.setRefundPolicy(policies.getRefundPolicy());
        termsAndPoliciesResponseDto.setCookiePolicy(policies.getCookiePolicy());
        termsAndPoliciesResponseDto.setCommunityGuidelines(policies.getCommunityGuidelines());

        return termsAndPoliciesResponseDto;
    }
}
