package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.dto.InquiryRequestDto;
import com.project.kisan_setu.dto.InquiryResponseDto;
import com.project.kisan_setu.entity.BuyerInquiry;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.enums.InquiryStatus;
import com.project.kisan_setu.repository.BuyerInquiryRepository;
import com.project.kisan_setu.repository.ListingRepository;
import com.project.kisan_setu.service.InquiryService;
import com.project.kisan_setu.util.ValidatorMethods;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService {
    private final ValidatorMethods validatorMethods;
    private final BuyerInquiryRepository buyerInquiryRepository;
    private final ListingRepository listingRepository;
    private static final Logger logger = LoggerFactory.getLogger(InquiryServiceImpl.class);
    @Override
    public InquiryResponseDto createInquiry(InquiryRequestDto request, Long userId) {
        logger.info("Validating user for creating inquiry...");
        User buyer = validatorMethods.validateUserById(userId);
        Listing listing = validatorMethods.validateExists(request.getListingId());

        System.out.println("=================================");
        System.out.println("Listing ID: " + listing.getListingId());
        System.out.println("Listing Quantity: " + listing.getQuantity());
        System.out.println("Listing Remaining Quantity: " + listing.getRemainingQuantity());
        System.out.println("Requested Quantity: " + request.getQuantityRequested());
        System.out.println("=================================");

        logger.info("Checking inquiry validations...");
        if (listing.getSeller().getUserId().equals(buyer.getUserId())) {
            throw new RuntimeException("You cannot send inquiry to your own listing");
        }
        BigDecimal currentRemaining = listing.getRemainingQuantity();
        BigDecimal requestedQuantity = request.getQuantityRequested();
        BigDecimal remainingQuantity = currentRemaining.subtract(requestedQuantity);

        if (remainingQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Not enough quantity available. Remaining: " + currentRemaining);
        }
        listing.setRemainingQuantity(remainingQuantity);
        listingRepository.save(listing);


        BuyerInquiry inquiry = new BuyerInquiry();
        inquiry.setListing(listing);
        inquiry.setBuyer(buyer);
        inquiry.setQuantityRequested(request.getQuantityRequested());
        inquiry.setStatus(InquiryStatus.PENDING);
        inquiry.setCreatedAt(LocalDateTime.now());
        inquiry.setRemainingQuantity(remainingQuantity);
        BuyerInquiry saved = buyerInquiryRepository.save(inquiry);

        logger.info("Inquiry creation success...");
        return InquiryResponseDto.builder()
                .inquiryId(inquiry.getInquiryId())
                .listingId(listing.getListingId())
                .buyerName(inquiry.getBuyer().getFullName())
                .cropName(inquiry.getListing().getCropName())
                .quantityRequested(saved.getQuantityRequested())
                .inquiryTime(inquiry.getInquiryTime())
                .status(InquiryStatus.valueOf(saved.getStatus().name()))
                .remainingQuantity(remainingQuantity)
                .build();
    }

    @Override
    public List<InquiryResponseDto> getSellerInquiries(Long userId) {
        logger.info("Validating user to get seller inquiries");
        User seller = validatorMethods.validateUserById(userId);

        logger.info("Checking if inquires exists in DB or not...");
        List<BuyerInquiry> inquiries = buyerInquiryRepository
                .findByListingSellerUserId(seller.getUserId());

        // Map entity → DTO
        logger.info("Fetching seller inquires success...");
        return inquiries.stream()
                .map(inq -> new InquiryResponseDto(
                        inq.getInquiryId(),
                        inq.getListing().getListingId(),
                        inq.getBuyer().getFullName(),
                        inq.getListing().getCropName(),
                        inq.getQuantityRequested(),
                        inq.getInquiryTime(),
                        inq.getStatus(),
                        inq.getRemainingQuantity()
                ))
                .toList();
    }


}
