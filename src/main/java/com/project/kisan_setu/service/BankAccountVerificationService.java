package com.project.kisan_setu.service;
import com.project.kisan_setu.dto.RequestDto.BankAccountRequestDto;
import com.project.kisan_setu.dto.ResponseDto.BankAccountResponseDto;

import java.util.List;

public interface BankAccountVerificationService {
    BankAccountResponseDto submitBankAccount(BankAccountRequestDto dto);
    BankAccountResponseDto getStatus();
    BankAccountResponseDto approveBank(Long userId);
    BankAccountResponseDto rejectBank(Long userId);
    List<BankAccountResponseDto> getPendingVerifications();
}
