package com.project.kisan_setu.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter //getters
@Setter //setters
@NoArgsConstructor //needed by jackson
@AllArgsConstructor
public class ProductListingDto {

    // Product Info
    @NotBlank(message = "crop name is required")
    private String cropName;
    @NotBlank(message = "variety is required")
    private String variety;
    @NotBlank(message = "grade is required")
    private String grade;
    @NotBlank(message = "harvest date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate harvestDate;
}

