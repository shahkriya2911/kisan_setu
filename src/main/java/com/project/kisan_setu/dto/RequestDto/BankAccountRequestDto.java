package com.project.kisan_setu.dto.RequestDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountRequestDto {
    @NotBlank(message = "Bank name cannot be empty")
    private String bankName;
    @NotBlank(message = "account number cannot be empty")
    private String accountNumber;
    @NotBlank(message = "ifsc code cannot be empty")
    private String ifscCode;
    @NotBlank(message = "account holder name cannot be empty")
    private String accountHolderName;
    @NotBlank(message = "UPI ID cannot be empty")
    private String upiId;
    @NotNull(message = "aadhaar card cannot be empty")
    private MultipartFile aadhaarCard;
    @NotNull(message = "Pan card cannot be empty")
    private MultipartFile panCard;
}
