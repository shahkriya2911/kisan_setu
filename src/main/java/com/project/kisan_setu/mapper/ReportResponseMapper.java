package com.project.kisan_setu.mapper;

import com.project.kisan_setu.dto.ResponseDto.ReportResponseDto;
import com.project.kisan_setu.entity.Report;

public class ReportResponseMapper {

    public static ReportResponseDto mapReportToDto(Report report,
                                       boolean isBuyerReported,
                                       boolean isSellerReported) {
        if(report == null)
        {
            return null;
        }

        return ReportResponseDto.builder()
                .reportId(report.getReportId())
                .buyerName(report.getBuyer() != null ? report.getBuyer().getFullName() : null)
                .sellerName(report.getSeller() != null ? report.getSeller().getFullName() : null)
                .issueType(report.getReason())
                .status(report.getReportStatus() != null ? report.getReportStatus().name() : null)
                .isBuyerReported(isBuyerReported)
                .isSellerReported(isSellerReported)
                .createdAt(report.getCreatedAt())
                .build();
    }

}
