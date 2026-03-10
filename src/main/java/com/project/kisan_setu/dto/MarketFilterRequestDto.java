package com.project.kisan_setu.dto;

import com.project.kisan_setu.enums.SaleType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarketFilterRequestDto {

    private String cropId;
    private Double minPrice;
    private Double maxPrice;
    private Long stateId;
    private Long districtId;
    private Integer minQuantity;
    private SaleType saleType;
}
