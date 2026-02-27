package com.project.kisan_setu.dto;

import com.project.kisan_setu.enums.SaleType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarketFilterRequestDto {

    private String cropType;
    private Double minPrice;
    private Double maxPrice;
    private String location;
    private Integer minQuantity;
    private SaleType saleType;
}
