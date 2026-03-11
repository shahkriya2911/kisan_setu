package com.project.kisan_setu.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KycStatusResponseDto {

    private boolean fullyVerified;
    private String overallMessage;
    private boolean aadhaarVerified;
    private String aadhaarNumber;
    private boolean panVerified;
    private String panNumber;
    private boolean bankVerified;
    private String bankAccountStatus;
}