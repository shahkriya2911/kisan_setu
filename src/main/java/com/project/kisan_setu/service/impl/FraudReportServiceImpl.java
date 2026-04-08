package com.project.kisan_setu.service.impl;
import com.project.kisan_setu.dto.RequestDto.FraudReportRequestDto;
import com.project.kisan_setu.dto.ResponseDto.FraudReportResponseDto;
import com.project.kisan_setu.entity.FraudReport;
import com.project.kisan_setu.entity.FraudType;
import com.project.kisan_setu.repository.FraudReportRepository;
import com.project.kisan_setu.repository.FraudTypeRepository;
import com.project.kisan_setu.service.FraudReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FraudReportServiceImpl implements FraudReportService {
    private final FraudTypeRepository fraudTypeRepository;
    private final FraudReportRepository fraudReportRepository;


    @Override
    public FraudReportResponseDto createReport(FraudReportRequestDto dto, Long currentUserId) {
       FraudType fraudType = fraudTypeRepository.findById(dto.getFraudTypeId())
                .orElseThrow(() -> new RuntimeException("Invalid Fraud Type"));

       FraudReport report = new FraudReport();
       report.setReportedBy(currentUserId);
       report.setReportedUser(dto.getReportedUserId());
       report.setFraudType(fraudType);
       report.setDescription(dto.getDescription());
       report.setStatus("OPEN");

        FraudReport saved = fraudReportRepository.save(report);

        return convertToDto(saved);

    }
    @Override
    public List<FraudReportResponseDto> getAllReports() {
        List<FraudReport> reports = fraudReportRepository.findAll();

        List<FraudReportResponseDto> responseList = new ArrayList<>();

        for (FraudReport report : reports) {
            responseList.add(convertToDto(report));
        }

        return responseList;
    }
    private FraudReportResponseDto convertToDto(FraudReport fraudReport)
    {
        FraudReportResponseDto dto = new FraudReportResponseDto();
        dto.setId(fraudReport.getId());
        dto.setReportedBy(fraudReport.getReportedBy());
        dto.setReportedUser(fraudReport.getReportedUser());
        dto.setFraudType(String.valueOf(fraudReport.getFraudType()));
        dto.setDescription(fraudReport.getDescription());
        dto.setStatus(fraudReport.getStatus());
        dto.setCreatedAt(fraudReport.getCreatedAt());

        return dto;

    }
}
