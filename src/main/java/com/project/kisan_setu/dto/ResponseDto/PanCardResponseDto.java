package com.project.kisan_setu.dto.ResponseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PanCardResponseDto {
    private Long userId;
    private String panNumber;
    private String nameOnPan;
    private String dateOfBirth;
    private String panImagePath;
    private Boolean verified;
    private LocalDateTime submittedAt;
    private LocalDateTime verifiedAt;
    private String message;
}