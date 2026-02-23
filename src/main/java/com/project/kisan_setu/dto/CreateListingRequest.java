package com.project.kisan_setu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateListingRequest {
    @NotBlank
    private ProductListingDto product;
    @NotNull(message = "product is required")
    private QualityPricingListingDto pricing;
    @NotBlank(message = "location is required")
    QualityLocationListingDto location;
}
