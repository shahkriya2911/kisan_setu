package com.project.kisan_setu.service.impl;
import com.project.kisan_setu.dto.ResponseDto.DisputeDto;
import com.project.kisan_setu.entity.Dispute;
import com.project.kisan_setu.enums.DisputeStatus;
import com.project.kisan_setu.repository.DisputeRepository;
import com.project.kisan_setu.service.AdminDisputeService;
import com.project.kisan_setu.util.ValidatorMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDisputeServiceImpl implements AdminDisputeService {
    private final DisputeRepository disputeRepository;
    private ValidatorMethods validatorMethods;
    @Override
    public List<DisputeDto> getAllDisputes(String status) {
        validatorMethods.validateAdminAccess();
        List<Dispute> disputes;

        if (status != null) {
            disputes = disputeRepository.findByStatus(DisputeStatus.valueOf(status));
        } else {
            disputes = disputeRepository.findAll();
        }

        return disputes.stream().map(d -> {
            DisputeDto dto = new DisputeDto();

            dto.setDisputeId(d.getDisputeId());
            dto.setOrderId(d.getOrder().getOrderId());
            dto.setBuyerName(d.getBuyer().getFullName());
            dto.setSellerName(d.getSeller().getFullName());
            dto.setIssueType(d.getIssueType());
            dto.setDisputeStatus(d.getStatus().name());
            dto.setCreatedDate(d.getCreatedAt());
            return dto;
        }).toList();
    }
    @Override
    public List<DisputeDto> getOpenDisputes() {
        validatorMethods.validateAdminAccess();

        List<Dispute> disputes = disputeRepository.findByStatus(DisputeStatus.OPEN);

        return disputes.stream().map(d -> {
            DisputeDto dto = new DisputeDto();

            dto.setDisputeId(d.getDisputeId());
            dto.setOrderId(d.getOrder().getOrderId());

            dto.setBuyerName(d.getBuyer().getFullName());
            dto.setSellerName(d.getSeller().getFullName());

            dto.setIssueType(d.getIssueType());
            dto.setDisputeStatus(d.getStatus().name());

            dto.setCreatedDate(d.getCreatedAt());

            return dto;
        }).toList();
    }
    @Override
    public List<DisputeDto> getResolvedDisputes() {
        validatorMethods.validateAdminAccess();

        List<Dispute> disputes = disputeRepository.findByStatus(DisputeStatus.RESOLVED);

        return disputes.stream().map(d -> {
            DisputeDto dto = new DisputeDto();

            dto.setDisputeId(d.getDisputeId());
            dto.setOrderId(d.getOrder().getOrderId());

            dto.setBuyerName(d.getBuyer().getFullName());
            dto.setSellerName(d.getSeller().getFullName());

            dto.setIssueType(d.getIssueType());
            dto.setDisputeStatus(d.getStatus().name());

            dto.setCreatedDate(d.getCreatedAt());

            return dto;
        }).toList();
    }
    @Override
    public List<DisputeDto> getUnderReviewDisputes() {
        validatorMethods.validateAdminAccess();

        List<Dispute> disputes = disputeRepository.findByStatus(DisputeStatus.UNDER_REVIEW);

        return disputes.stream().map(d -> {
            DisputeDto dto = new DisputeDto();

            dto.setDisputeId(d.getDisputeId());
            dto.setOrderId(d.getOrder().getOrderId());

            dto.setBuyerName(d.getBuyer().getFullName());
            dto.setSellerName(d.getSeller().getFullName());

            dto.setIssueType(d.getIssueType());
            dto.setDisputeStatus(d.getStatus().name());

            dto.setCreatedDate(d.getCreatedAt());

            return dto;
        }).toList();
    }
}
