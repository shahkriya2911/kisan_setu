package com.project.kisan_setu.dto;

<<<<<<< Updated upstream:src/main/java/com/project/kisan_setu/dto/SellerAddCropResponseDto.java
=======
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
>>>>>>> Stashed changes:src/main/java/com/project/kisan_setu/entity/SellerAddCrop.java
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class SellerAddCropResponseDto {
    private Long id;
    private String cropName;
    private Double quantity;
    private Double basePrice;
    private LocalDateTime harvestDate;
    private String talika;
    private String village;
    private String taluka;
    private String district;
    private String state;
    private String status;
    private LocalDateTime createdAt;
<<<<<<< Updated upstream:src/main/java/com/project/kisan_setu/dto/SellerAddCropResponseDto.java
    private Long sellerId;
    private String sellerName;
=======

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;



>>>>>>> Stashed changes:src/main/java/com/project/kisan_setu/entity/SellerAddCrop.java
}
