package com.project.kisan_setu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor //constructor
@NoArgsConstructor //needed by jackson
@Getter //getters
@Setter //setters
public class SchemeRequestDto {

    //scheme info
    @NotBlank(message = "scheme title is required")
    private String schemeTitle;
    @NotBlank(message = "scheme full name is required")
    private String schemeFullName;
    @NotBlank(message = "scheme category is required")
    private String schemeCategory;
    @NotBlank(message = "scheme description is required")
    private String schemeDescription;
    @NotEmpty(message = "scheme benefits are required")
    private List<String> schemeBenefits;
    @NotBlank(message = "scheme eligibility is required")
    private String schemeEligibility;
    @NotBlank(message = "scheme state is required")
    private String schemeState;
    @NotBlank(message = "scheme official link is required")
    private String schemeOfficialLink;
}
