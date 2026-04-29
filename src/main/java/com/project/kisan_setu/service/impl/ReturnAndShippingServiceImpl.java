package com.project.kisan_setu.service.impl;
import com.project.kisan_setu.dto.RequestDto.ReturnAndShippingRequestDto;
import com.project.kisan_setu.dto.ResponseDto.ReturnAndShippingResponseDto;
import com.project.kisan_setu.entity.ReturnAndShipping;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.mapper.ReturnAndShippingMapper;
import com.project.kisan_setu.repository.ReturnAndShippingRepository;
import com.project.kisan_setu.service.ReturnAndShippingService;
import com.project.kisan_setu.util.ValidatorMethods;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReturnAndShippingServiceImpl implements ReturnAndShippingService {
    private final ReturnAndShippingRepository returnAndShippingRepository;
    private final ValidatorMethods validatorMethods;

    public ReturnAndShippingServiceImpl(ReturnAndShippingRepository returnAndShippingRepository, ValidatorMethods validatorMethods) {
        this.returnAndShippingRepository = returnAndShippingRepository;
        this.validatorMethods = validatorMethods;
    }

    @Override
    public List<ReturnAndShippingResponseDto> getMyReturnAndShipping() {
        Long userId = validatorMethods.getCurrentUserId();
        return returnAndShippingRepository.findByUserUserId(userId)
                .stream()
                .map(ReturnAndShippingMapper::toDto)
                .toList();
    }

    @Override
    public ReturnAndShippingResponseDto getReturnAndShippingById(Long returnAndShippingId) {
        ReturnAndShipping returnAndShipping = validatorMethods.getReturnAndShippingById(returnAndShippingId);
        validateOwnership(returnAndShipping);
        return ReturnAndShippingMapper.toDto(returnAndShipping);
    }

    @Override
    public ReturnAndShippingResponseDto postReturnAndShipping(ReturnAndShippingRequestDto returnAndShippingRequestDto){
        Long userId = validatorMethods.getCurrentUserId();
        User user = validatorMethods.validateUserById(userId);
        ReturnAndShipping returnAndShipping = ReturnAndShippingMapper.toEntity(returnAndShippingRequestDto);
        returnAndShipping.setUser(user);
        ReturnAndShipping saved = returnAndShippingRepository.save(returnAndShipping);
        return ReturnAndShippingMapper.toDto(saved);
    }

    @Override
    public ReturnAndShippingResponseDto updateReturnAndShipping(Long returnAndShippingId,ReturnAndShippingRequestDto returnAndShippingRequestDto){
        ReturnAndShipping returnAndShipping = validatorMethods.getReturnAndShippingById(returnAndShippingId);
        validateOwnership(returnAndShipping);
        ReturnAndShippingMapper.updateEntity(returnAndShipping, returnAndShippingRequestDto);
        ReturnAndShipping saved = returnAndShippingRepository.save(returnAndShipping);
        return ReturnAndShippingMapper.toDto(saved);
    }

    @Override
    public void deleteReturnAndShipping(Long returnAndShippingId){
        ReturnAndShipping returnAndShipping = validatorMethods.getReturnAndShippingById(returnAndShippingId);
        validateOwnership(returnAndShipping);
        returnAndShippingRepository.delete(returnAndShipping);
    }

    private void validateOwnership(ReturnAndShipping returnAndShipping) {
        Long currentUserId = validatorMethods.getCurrentUserId();
        if (returnAndShipping.getUser() == null
                || !currentUserId.equals(returnAndShipping.getUser().getUserId())) {
            throw new UserException("You are not authorized to access this return and shipping entry", HttpStatus.UNAUTHORIZED);
        }
    }
}
