package com.project.kisan_setu.service.impl;
import com.project.kisan_setu.dto.RequestDto.ChangingLanguageRequestDto;
import com.project.kisan_setu.dto.ResponseDto.ChangingLanguageResponseDto;
import com.project.kisan_setu.entity.ChangingLanguage;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.mapper.ChangingLanguageMapper;
import com.project.kisan_setu.repository.ChangingLanguageRepository;
import com.project.kisan_setu.service.ChangingLanguageService;
import com.project.kisan_setu.util.ValidatorMethods;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ChangingLanguageServiceImpl implements ChangingLanguageService {
    private final ChangingLanguageRepository changingLanguageRepository;
    private final ValidatorMethods validatorMethods;

    public ChangingLanguageServiceImpl(ChangingLanguageRepository changingLanguageRepository,
                                       ValidatorMethods validatorMethods) {
        this.changingLanguageRepository = changingLanguageRepository;
        this.validatorMethods = validatorMethods;
    }

    @Override
    public ChangingLanguageResponseDto getMyChangingLanguage() {
        Long userId = validatorMethods.getCurrentUserId();
        ChangingLanguage changingLanguage = changingLanguageRepository.findByUserUserId(userId)
                .orElseThrow(() -> new UserException("Language preferences not found", HttpStatus.NOT_FOUND));
        return ChangingLanguageMapper.toDto(changingLanguage);
    }

    @Override
    public ChangingLanguageResponseDto postChangingLanguage(ChangingLanguageRequestDto changingLanguageRequestDto){
        Long userId = validatorMethods.getCurrentUserId();
        User user = validatorMethods.validateUserById(userId);
        ChangingLanguage changingLanguage = changingLanguageRepository.findByUserUserId(userId)
                .orElseGet(ChangingLanguage::new);
        ChangingLanguageMapper.updateEntity(changingLanguage, changingLanguageRequestDto);
        changingLanguage.setUser(user);
        ChangingLanguage saved = changingLanguageRepository.save(changingLanguage);
        return ChangingLanguageMapper.toDto(saved);
    }

    @Override
    public ChangingLanguageResponseDto updateChangingLanguage(Long changingLanguageId,ChangingLanguageRequestDto changingLanguageRequestDto) {
        ChangingLanguage changingLanguage = changingLanguageRepository.findById(changingLanguageId)
                .orElseThrow(() -> new UserException("Language preferences not found",HttpStatus.NOT_FOUND));
        validateOwnership(changingLanguage);
        ChangingLanguageMapper.updateEntity(changingLanguage, changingLanguageRequestDto);
        ChangingLanguage saved = changingLanguageRepository.save(changingLanguage);
        return ChangingLanguageMapper.toDto(saved);
    }

    private void validateOwnership(ChangingLanguage changingLanguage) {
        Long currentUserId = validatorMethods.getCurrentUserId();
        if (changingLanguage.getUser() == null
                || !currentUserId.equals(changingLanguage.getUser().getUserId())) {
            throw new UserException("You are not authorized to access language preferences",HttpStatus.FORBIDDEN);
        }
    }

}
