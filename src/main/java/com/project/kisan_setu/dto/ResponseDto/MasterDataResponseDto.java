package com.project.kisan_setu.dto.ResponseDto;
import com.project.kisan_setu.dto.IdNameDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MasterDataResponseDto {

    private List<IdNameDto> crops;
    private List<IdNameDto> packagingTypes;
    private List<IdNameDto> storageTypes;
    private List<IdNameDto> units;
    private List<IdNameDto> states;
    private List<IdNameDto> districts;

}