package com.project.kisan_setu.mapper;
import com.project.kisan_setu.dto.RequestDto.IdNameDto;

public class MasterMapper {

    public static IdNameDto toDto(Long id, String name) {

        return new IdNameDto(id, name);

    }
}