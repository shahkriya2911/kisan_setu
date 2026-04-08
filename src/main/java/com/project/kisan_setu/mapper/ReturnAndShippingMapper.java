package com.project.kisan_setu.mapper;
import com.project.kisan_setu.dto.RequestDto.ReturnAndShippingRequestDto;
import com.project.kisan_setu.dto.ResponseDto.ReturnAndShippingResponseDto;
import com.project.kisan_setu.entity.ReturnAndShipping;

public class ReturnAndShippingMapper {
    public static ReturnAndShippingResponseDto toDto(ReturnAndShipping returnAndShipping) {
        ReturnAndShippingResponseDto returnAndShippingResponseDto = new ReturnAndShippingResponseDto();
        returnAndShippingResponseDto.setReturnAndShippingId(returnAndShipping.getReturnAndShippingId());
        returnAndShippingResponseDto.setShippingAddress(returnAndShipping.getShippingAddress());
        returnAndShippingResponseDto.setReturnWindow(returnAndShipping.getReturnWindow());
        return returnAndShippingResponseDto;
    }

    public static ReturnAndShipping toEntity(ReturnAndShippingRequestDto returnAndShippingRequestDto){
        ReturnAndShipping returnAndShipping = new ReturnAndShipping();
        returnAndShipping.setShippingAddress(returnAndShippingRequestDto.getShippingAddress());
        returnAndShipping.setReturnWindow(returnAndShippingRequestDto.getReturnWindow());
        return returnAndShipping;
    }

    public static ReturnAndShipping updateEntity(ReturnAndShipping returnAndShipping, ReturnAndShippingRequestDto returnAndShippingRequestDto) {
        returnAndShipping.setShippingAddress(returnAndShippingRequestDto.getShippingAddress());
        returnAndShipping.setReturnWindow(returnAndShippingRequestDto.getReturnWindow());
        return returnAndShipping;
    }
}
