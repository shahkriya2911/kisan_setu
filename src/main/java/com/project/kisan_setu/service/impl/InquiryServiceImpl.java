package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.InquiryRequestDto;
import com.project.kisan_setu.dto.InquiryResponseDto;
import com.project.kisan_setu.entity.BuyerInquiry;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.enums.InquiryStatus;
import com.project.kisan_setu.repository.BuyerInquiryRepository;
import com.project.kisan_setu.service.InquiryService;
import com.project.kisan_setu.util.ValidatorMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService {
    private final ValidatorMethods validatorMethods;
    private final BuyerInquiryRepository buyerInquiryRepository;
    @Override
    public InquiryResponseDto createInquiry(InquiryRequestDto request, Long userId) {
        User buyer = validatorMethods.validateUserById(userId);
        Listing listing = validatorMethods.validateExists(request.getListingId());
        if (listing.getSeller().getUserId().equals(buyer.getUserId())) {
            throw new RuntimeException("You cannot send inquiry to your own listing");
        }
        BuyerInquiry inquiry = new BuyerInquiry();
        inquiry.setListing(listing);
        inquiry.setBuyer(buyer);
        inquiry.setQuantityRequested(request.getQuantityRequested());
        inquiry.setStatus(InquiryStatus.PENDING);
        inquiry.setCreatedAt(LocalDateTime.now());
        BuyerInquiry saved = buyerInquiryRepository.save(inquiry);

        return InquiryResponseDto.builder()
                .listingId(listing.getListingId())
                .buyerName(inquiry.getBuyer().getFullName())
                .quantityRequested(saved.getQuantityRequested())
                .status(InquiryStatus.valueOf(saved.getStatus().name()))
                .build();
    }

    @Override
    public List<InquiryResponseDto> getSellerInquiries(Long userId) {

        User seller = validatorMethods.validateUserById(userId);

        List<BuyerInquiry> inquiries = buyerInquiryRepository
                .findByListingSellerUserId(seller.getUserId());

        // Map entity → DTO
        return inquiries.stream()
                .map(inq -> new InquiryResponseDto(
                        inq.getInquiryId(),
                        inq.getListing().getListingId(),
                        inq.getBuyer().getFullName(),
                        inq.getListing().getCropName(),
                        inq.getQuantityRequested(),
                        inq.getInquiryTime(),
                        inq.getStatus()
                ))
                .toList();
    }


}
