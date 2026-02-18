package com.project.kisan_setu.dto;

import lombok.Data;

@Data
public class CreateListingRequest {
    private ProductListingDto product;
    private QualityPricingListingDto pricing;
    QualityLocationListingDto location;
}
