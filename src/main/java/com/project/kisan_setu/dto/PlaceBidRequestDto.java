package com.project.kisan_setu.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter //getters
@Setter //setters
@NoArgsConstructor //needed by jackson
@AllArgsConstructor
public class PlaceBidRequestDto {
    @NotNull(message = "bid amount is required")
    private Double bidAmount;
}
