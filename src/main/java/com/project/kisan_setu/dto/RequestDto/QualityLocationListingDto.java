package com.project.kisan_setu.dto.RequestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "state is required")
    private Long stateId;
    @NotNull(message = "district is required")
    private Long districtId;
    @NotBlank(message = "packaging type is required")
    private String packagingId;
    @NotBlank(message = "storage type is required")
    private String storageId;
    @NotBlank(message = "pickup method is required")
    private String pickupMethod;
}
