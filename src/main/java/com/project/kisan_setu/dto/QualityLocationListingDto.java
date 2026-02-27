package com.project.kisan_setu.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QualityLocationListingDto {

    //quality and location info
    @NotBlank(message = "state is required")
    private String state;
    @NotBlank(message = "packaging type is required")
    private String packagingType;
    @NotBlank(message = "district is required")
    private String district;
    @NotBlank(message = "storage type is required")
    private String storageType;
    @NotBlank(message = "pickup method is required")
    private String pickupMethod;
}
