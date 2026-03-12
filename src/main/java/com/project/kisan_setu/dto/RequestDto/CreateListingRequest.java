package com.project.kisan_setu.dto.RequestDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateListingRequest {

    @Valid
    @NotNull(message = "Product details are required")
    private ProductListingDto product;

    @Valid
    @NotNull(message = "Pricing details are required")
    private QualityPricingListingDto pricing;

    @Valid
    @NotNull(message = "Location details are required")
    private QualityLocationListingDto location;

    // Optional
    @Valid
    private List<ProductImageListingDto> images;

    // Optional
    @Valid
    private QualityCertificateListingDto certificate;

    @Valid
    private String description; //
}