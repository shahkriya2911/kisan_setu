package com.project.kisan_setu.dto;

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
    private String schemeTitle;
    private String schemeFullName;
    private String schemeCategory;
    private String schemeDescription;
    private List<String> schemeBenefits;
    private String schemeEligibility;
    private String schemeState;
    private String schemeOfficialLink;
}
