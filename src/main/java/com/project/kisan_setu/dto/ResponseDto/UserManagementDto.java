package com.project.kisan_setu.dto.ResponseDto;
import com.project.kisan_setu.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserManagementDto {

    private Long userId;
    private String name;
    private String userType;
    private String location;
    private String verification; // Verified / Unverified
    private long listings;
    private long transactions;
    private UserStatus status;



}