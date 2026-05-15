package com.project.kisan_setu.service.impl;
import com.project.kisan_setu.dto.RequestDto.BuyingRequirementRequestDto;
import com.project.kisan_setu.dto.RequestDto.PlaceBidRequestDto;
import com.project.kisan_setu.dto.ResponseDto.BidResponseDto;
import com.project.kisan_setu.dto.ResponseDto.BuyerListingResponseDto;
import com.project.kisan_setu.dto.ResponseDto.BuyerRequirementSummaryDto;
import com.project.kisan_setu.dto.ResponseDto.ProductImageResponseDto;
import com.project.kisan_setu.dto.ResponseDto.QualityCertificateResponseDto;
import com.project.kisan_setu.dto.ResponseDto.BuyingRequirementResponseDto;
import com.project.kisan_setu.embedded.ListingImage;
import com.project.kisan_setu.entity.BuyingRequirement;
import com.project.kisan_setu.entity.CropMaster;
import com.project.kisan_setu.entity.DistrictMaster;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.entity.UnitMaster;
import com.project.kisan_setu.entity.StateMaster;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.Bid;
import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.enums.PurchaseType;
import com.project.kisan_setu.enums.BidStatus;
import com.project.kisan_setu.enums.NotificationStatus;
import com.project.kisan_setu.enums.RequirementStatus;
import com.project.kisan_setu.enums.SaleType;
import com.project.kisan_setu.enums.Urgency;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.mapper.BuyingRequirementMapper;
import com.project.kisan_setu.mapper.ListingMapper;
import com.project.kisan_setu.repository.BidRepository;
import com.project.kisan_setu.repository.BuyingRequirementRepository;
import com.project.kisan_setu.repository.ListingRepository;
import com.project.kisan_setu.service.BuyerService;
import com.project.kisan_setu.service.NotificationService;
import com.project.kisan_setu.util.ValidatorMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuyerServiceImpl implements BuyerService {
    private final ListingRepository listingRepository;
    private final BidRepository bidRepository;
    private final ValidatorMethods validatorMethods;
    private final BuyingRequirementRepository buyingRequirementRepository;
    private final NotificationService notificationService;

    // Post Requirement
    @Override
    public BuyingRequirementResponseDto postRequirement(BuyingRequirementRequestDto dto) {

        Long userId = validatorMethods.getCurrentUserId();

        User buyer = validatorMethods.validateUserById(userId);
        CropMaster crop = validatorMethods.validateCrop(Long.valueOf(dto.getCropId()));
        UnitMaster unit = validatorMethods.validateUnit(Long.valueOf(dto.getUnitId()));
        StateMaster state = validatorMethods.validateState(Long.valueOf(dto.getStateId()));
        DistrictMaster district = validatorMethods.validateDistrict(Long.valueOf(dto.getDistrictId()));

        BuyingRequirement requirement = BuyingRequirementMapper.toEntity(dto, buyer, crop, unit, state, district);

        BuyingRequirement saved = buyingRequirementRepository.save(requirement);

        return BuyingRequirementMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BuyingRequirementResponseDto> getMyRequirements(Pageable pageable, String cropName) {
        Long userId = validatorMethods.getCurrentUserId();
        String filterCrop = normalizeCropFilter(cropName);

        Page<BuyingRequirement> requirements = filterCrop == null
                ? buyingRequirementRepository.findByBuyerUserId(userId, pageable)
                : buyingRequirementRepository.findByBuyerUserIdAndCrop_CropNameContainingIgnoreCase(
                        userId, filterCrop, pageable);

        return requirements.map(BuyingRequirementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public BuyerRequirementSummaryDto getMyRequirementSummary() {
        Long userId = validatorMethods.getCurrentUserId();

        Long totalRequirements = buyingRequirementRepository
                .countByBuyerUserIdAndRequirementStatus(userId, RequirementStatus.OPEN);
        Long normalRequirements = buyingRequirementRepository
                .countByBuyerUserIdAndRequirementStatusAndUrgency(
                        userId, RequirementStatus.OPEN, Urgency.NORMAL);
        Long urgentRequirements = buyingRequirementRepository
                .countByBuyerUserIdAndRequirementStatusAndUrgency(
                        userId, RequirementStatus.OPEN, Urgency.URGENT);

        return new BuyerRequirementSummaryDto(
                totalRequirements,
                normalRequirements,
                urgentRequirements);
    }

    @Override
    @Transactional
    public void deleteRequirement(Long requirementId) {
        Long userId = validatorMethods.getCurrentUserId();
        BuyingRequirement requirement = buyingRequirementRepository.findById(requirementId)
                .orElseThrow(() -> new UserException("Requirement not found", HttpStatus.NOT_FOUND));

        if (requirement.getBuyer() == null || !userId.equals(requirement.getBuyer().getUserId())) {
            throw new UserException("You are not authorized to delete this requirement", HttpStatus.FORBIDDEN);
        }

        buyingRequirementRepository.delete(requirement);
    }

    @Override
    @Transactional
    public Page<BuyerListingResponseDto> getActiveAuctionListings(Long userId, Pageable pageable, String cropName) {

        String filterCrop = buildCropNamePattern(cropName);

        List<Listing> listings = listingRepository
                .findActiveAuctionListings(SaleType.AUCTION, AuctionStatus.ACTIVE, userId, filterCrop);

        LocalDateTime now = LocalDateTime.now();
        boolean updated = false;
        for (Listing listing : listings) {

            boolean isTimeExpired = listing.getAuctionEndTime() != null &&
                    listing.getAuctionEndTime().isBefore(now);

            boolean isAlreadySold = listing.getIsSold() != null &&
                    listing.getIsSold();

            if (isTimeExpired || isAlreadySold) {
                listing.setStatus(AuctionStatus.EXPIRED);
                updated = true;
            }
        }

        if (updated) {
            listingRepository.saveAll(listings);
        }

        List<BuyerListingResponseDto> dtoList = listings.stream()
                .filter(l -> l.getStatus() == AuctionStatus.ACTIVE)
                .filter(l -> l.getSeller() == null || !userId.equals(l.getSeller().getUserId()))
                .map(this::toBuyerListingResponse)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), dtoList.size());
        List<BuyerListingResponseDto> pageContent = start < dtoList.size() ? dtoList.subList(start, end) : List.of();

        return new PageImpl<>(pageContent, pageable, dtoList.size());
    }

    @Override
    @Transactional
    public Page<BuyerListingResponseDto> getActiveFixedListings(Long userId, Pageable pageable, String cropName) {

        String filterCrop = buildCropNamePattern(cropName);

        List<Listing> listings = listingRepository
                .findActiveFixedListings(SaleType.FIXED, AuctionStatus.ACTIVE, userId, filterCrop);

        boolean updated = false;
        for (Listing listing : listings) {

            boolean isOutOfStock = listing.getQuantity() != null &&
                    listing.getQuantity().compareTo(BigDecimal.ZERO) <= 0;

            if (isOutOfStock) {
                listing.setStatus(AuctionStatus.EXPIRED);
                updated = true;
            }
        }

        if (updated) {
            listingRepository.saveAll(listings);
        }

        List<BuyerListingResponseDto> dtoList = listings.stream()
                .filter(l -> l.getStatus() == AuctionStatus.ACTIVE)
                .filter(l -> l.getSeller() == null || !userId.equals(l.getSeller().getUserId()))
                .map(this::toBuyerListingResponse)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), dtoList.size());
        List<BuyerListingResponseDto> pageContent = start < dtoList.size() ? dtoList.subList(start, end) : List.of();

        return new PageImpl<>(pageContent, pageable, dtoList.size());
    }

    private String buildCropNamePattern(String cropName) {
        if (cropName == null || cropName.isBlank()) {
            return null;
        }
        return cropName.trim().toLowerCase(Locale.ROOT) + "%";
    }

    private String normalizeCropFilter(String cropName) {
        if (cropName == null || cropName.isBlank()) {
            return null;
        }
        return cropName.trim();
    }

    @Override
    @Transactional
    public void closeExpiredListings() {
        listingRepository.closeExpiredAuctions();
        listingRepository.closeExpiredFixedListings();
    }

    @Override
    public BuyerListingResponseDto getAuctionListingDetail(Long listingId) {
        Listing listing = validatorMethods.validateExists(listingId);
        if (listing.getSaleType() != SaleType.AUCTION) {
            throw new RuntimeException("Listing is not auction");
        }
        return toBuyerListingResponse(listing);
    }

    @Override
    public BuyerListingResponseDto getFixedListingDetail(Long listingId) {
        Listing listing = validatorMethods.validateExists(listingId);
        if (listing.getSaleType() != SaleType.FIXED) {
            throw new RuntimeException("Listing is not fixed");
        }
        return toBuyerListingResponse(listing);
    }

    @Override
    @Transactional
    public BidResponseDto placeBid(Long listingId, PlaceBidRequestDto dto) {

        Long userId = validatorMethods.getCurrentUserId();
        Listing listing = listingRepository.findByIdForUpdate(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        User buyer = validatorMethods.validateUserById(userId);

        // Seller cannot buy own listing
        if (listing.getSeller().getUserId().equals(buyer.getUserId())) {
            throw new RuntimeException("Seller cannot buy own listing");
        }
        if (listing.getBidAccepted()) {
            throw new RuntimeException("Bidding closed for this listing");
        }

        if (listing.getSaleType() != SaleType.AUCTION) {
            throw new RuntimeException("Invalid sale type");
        }

        if (listing.getAuctionEndTime() == null) {
            throw new RuntimeException("Auction end time not set");
        }

        if (LocalDateTime.now().isAfter(listing.getAuctionEndTime())) {
            throw new RuntimeException("Auction Time Ended");
        }

        Optional<Bid> latestBidOpt = bidRepository.findTopByListingListingIdOrderByBidIdDesc(listingId);
        if (latestBidOpt.isPresent()) {
            Bid latestBid = latestBidOpt.get();
            if (latestBid.getBuyer() != null
                    && latestBid.getBuyer().getUserId().equals(buyer.getUserId())) {
                throw new RuntimeException(
                        "You must wait for another buyer to place a bid before bidding again.");
            }
        }

        if (listing.getPurchaseType() == PurchaseType.WHOLE_LOT_ONLY) {

            if (dto.getBuyerAmount() == null) {
                throw new RuntimeException("Bid amount required");
            }

            BigDecimal basePrice = listing.getTotalBasePrice();
            if (basePrice == null && listing.getPricePerKg() != null && listing.getQuantity() != null) {
                basePrice = listing.getPricePerKg()
                        .multiply(listing.getQuantity());
            }
            if (basePrice == null) {
                throw new RuntimeException("Base price not set");
            }

            Optional<Bid> highestBidOpt = bidRepository
                    .findTopByListingListingIdOrderByBuyerAmountDesc(listingId);

            if (listing.getMinimumBidIncrement() == null) {
                throw new RuntimeException("Minimum bid increment not set");
            }
            if (listing.getMaximumBidIncrement() == null) {
                throw new RuntimeException("Maximum bid increment not set");
            }
            if (listing.getMaximumBidIncrement().compareTo(listing.getMinimumBidIncrement()) < 0) {
                throw new RuntimeException("Invalid bid increment range");
            }

            BigDecimal minimumAllowedBid;
            BigDecimal maximumAllowedBid;

            if (highestBidOpt.isEmpty()) {
                minimumAllowedBid = basePrice.add(listing.getMinimumBidIncrement());
                maximumAllowedBid = basePrice.add(listing.getMaximumBidIncrement());
            } else {
                BigDecimal currentHighest = highestBidOpt.get().getBuyerAmount();
                minimumAllowedBid = currentHighest.add(listing.getMinimumBidIncrement());
                maximumAllowedBid = currentHighest.add(listing.getMaximumBidIncrement());
            }

            if (dto.getBuyerAmount().compareTo(minimumAllowedBid) < 0
                    || dto.getBuyerAmount().compareTo(maximumAllowedBid) > 0) {
                throw new RuntimeException(
                        "Bid must be between " + minimumAllowedBid + " and " + maximumAllowedBid);
            }

            Bid bid = new Bid();
            bid.setBuyerAmount(dto.getBuyerAmount());
            bid.setBidTime(LocalDateTime.now());
            bid.setBidStatus(BidStatus.PENDING);
            bid.setListing(listing);
            bid.setBuyer(buyer);

            bidRepository.save(bid);
            notificationService.createNotification(listing.getSeller(),
                    "New bid placed on your listing",
                    NotificationStatus.BID_PLACED, listing, bid, null);

            return new BidResponseDto(
                    bid.getBidId(),
                    bid.getBuyer().getUserId(),
                    bid.getBuyerAmount(),
                    buyer.getFullName(),
                    bid.getBidTime(),
                    BidStatus.PENDING,
                    bid.getListing().getSeller().getUserId(),
                    bid.getListing().getListingId()
            );
        }

        throw new RuntimeException("Invalid purchase type");
    }

    private BuyerListingResponseDto toBuyerListingResponse(Listing listing) {
        BigDecimal basePrice = listing.getTotalBasePrice();
        if (basePrice == null && listing.getPricePerKg() != null && listing.getQuantity() != null) {
            basePrice = listing.getPricePerKg()
                    .multiply(listing.getQuantity());
        }

        BigDecimal resolvedBasePrice = basePrice != null ? basePrice : listing.getPricePerKg();
        BigDecimal noBidHighest = listing.getPurchaseType() == PurchaseType.WHOLE_LOT_ONLY
                ? resolvedBasePrice
                : listing.getPricePerKg();

        BigDecimal currentHighest = bidRepository
                .findTopByListingListingIdOrderByBuyerAmountDesc(listing.getListingId())
                .map(Bid::getBuyerAmount)
                .orElse(noBidHighest);

        List<ProductImageResponseDto> images = listing.getImages() == null
                ? List.of()
                : listing.getImages().stream()
                        .map(this::toImageResponse)
                        .collect(Collectors.toList());
        List<BidResponseDto> top5Bids = bidRepository
                .findTop5ByListingListingIdOrderByBuyerAmountDesc(listing.getListingId()) //
                .stream()
                .map(bid -> new BidResponseDto(
                        bid.getBidId(),
                        bid.getBuyer().getUserId(),
                        bid.getBuyerAmount(),
                        bid.getBuyer().getFullName(),
                        bid.getBidTime(),
                        bid.getBidStatus(), //
                        bid.getListing().getSeller().getUserId(),
                        bid.getListing().getListingId()
                ))
                .toList();
        for (int i=0;i<top5Bids.size();i++){
            if (i==0){
                top5Bids.get(i).setBidStatus(BidStatus.PENDING);
            }else {
                top5Bids.get(i).setBidStatus(BidStatus.OUTBID);
            }
        }

        return new BuyerListingResponseDto(
                listing.getListingId(),
                listing.getCrop().getCropName(),
                listing.getVariety(),
                listing.getState() != null ? listing.getState().getName() : null,
                listing.getQuantity(),
                listing.getUnit().getUnitName(),
                listing.getPackaging() != null ? listing.getPackaging().getPackagingType() : null,
                listing.getSaleType(),
                listing.getStorage() != null ? listing.getStorage().getStorageType() : null,
                listing.getHarvestDate(),
                listing.getMinimumBidIncrement(),
                listing.getMaximumBidIncrement(),
                listing.getGrade(),
                resolvedBasePrice,
                listing.getPricePerKg(),
                listing.getPurchaseType(),
                listing.getDistrict() != null ? listing.getDistrict().getName() : null,
                listing.getStatus(),
                listing.getAuctionEndTime(),
                currentHighest,
                images,
                ListingMapper.resolveMinimumOrderQuantity(listing),
                top5Bids,
                listing.getSeller().getFullName(),
                toCertificateResponse(listing));
    }

    private ProductImageResponseDto toImageResponse(ListingImage image) {
        ProductImageResponseDto dto = new ProductImageResponseDto();
        dto.setFileName(image.getFileName());
        dto.setFilePath(image.getFilePath());
        dto.setFileType(image.getFileType());
        dto.setIsPrimary(image.getIsPrimary());
        return dto;
    }

    private QualityCertificateResponseDto toCertificateResponse(Listing listing) {
        if (listing.getCertificate() == null) {
            return null;
        }

        QualityCertificateResponseDto dto = new QualityCertificateResponseDto();
        dto.setFileName(listing.getCertificate().getFileName());
        dto.setFilePath(listing.getCertificate().getFilePath());
        dto.setFileType(listing.getCertificate().getFileType());
        return dto;
    }

    private BigDecimal resolveCurrentHighestBid(Listing listing) {
        BigDecimal basePrice = listing.getTotalBasePrice();
        if (basePrice == null && listing.getPricePerKg() != null && listing.getQuantity() != null) {
            basePrice = listing.getPricePerKg().multiply(listing.getQuantity());
        }

        BigDecimal resolvedBasePrice = basePrice != null ? basePrice : listing.getPricePerKg();
        BigDecimal fallback = listing.getPurchaseType() == PurchaseType.WHOLE_LOT_ONLY
                ? resolvedBasePrice
                : listing.getPricePerKg();

        return bidRepository
                .findTopByListingListingIdOrderByBuyerAmountDesc(listing.getListingId())
                .map(Bid::getBuyerAmount)
                .orElse(fallback);
    }
}
