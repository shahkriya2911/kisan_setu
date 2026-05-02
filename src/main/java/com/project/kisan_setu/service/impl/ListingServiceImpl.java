package com.project.kisan_setu.service.impl;
import com.project.kisan_setu.dto.AuctionListingResponseDto;
import com.project.kisan_setu.dto.RequestDto.CreateListingRequest;
import com.project.kisan_setu.dto.RequestDto.ProductListingDto;
import com.project.kisan_setu.dto.RequestDto.QualityLocationListingDto;
import com.project.kisan_setu.dto.RequestDto.QualityPricingListingDto;
import com.project.kisan_setu.dto.RequestDto.ExtendAuctionDto;
import com.project.kisan_setu.dto.ResponseDto.BuyerContactResponseDto;
import com.project.kisan_setu.dto.ResponseDto.ListingResponseDto;
import com.project.kisan_setu.dto.ResponseDto.SellerListingDto;
import com.project.kisan_setu.dto.ResponseDto.SellerListingFixedDto;
import com.project.kisan_setu.dto.ResponseDto.BuyingRequirementResponseDto;
import com.project.kisan_setu.dto.ResponseDto.ListingSummaryResponseDto;
import com.project.kisan_setu.dto.ResponseDto.RecentBidResponseDto;
import com.project.kisan_setu.dto.ResponseDto.BidResponseDto;
import com.project.kisan_setu.dto.ResponseDto.DashboardDto;
import com.project.kisan_setu.embedded.ListingCertificate;
import com.project.kisan_setu.embedded.ListingImage;
import com.project.kisan_setu.entity.CropMaster;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.UnitMaster;
import com.project.kisan_setu.entity.StateMaster;
import com.project.kisan_setu.entity.DistrictMaster;
import com.project.kisan_setu.entity.PackagingMaster;
import com.project.kisan_setu.entity.StorageMaster;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.entity.Bid;
import com.project.kisan_setu.entity.BuyingRequirement;
import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.enums.BidStatus;
import com.project.kisan_setu.enums.PurchaseType;
import com.project.kisan_setu.enums.SaleType;
import com.project.kisan_setu.enums.RequirementStatus;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.mapper.BuyingRequirementMapper;
import com.project.kisan_setu.mapper.ListingMapper;
import com.project.kisan_setu.repository.BidRepository;
import com.project.kisan_setu.repository.ListingRepository;

import com.project.kisan_setu.repository.BuyingRequirementRepository;

import com.project.kisan_setu.service.FileStorageService;
import com.project.kisan_setu.service.ListingService;
import com.project.kisan_setu.util.ValidatorMethods;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListingServiceImpl implements ListingService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    private final ListingRepository listingRepository;
    private final BidRepository bidRepository;
    private final FileStorageService fileStorageService;
    private final ValidatorMethods validatorMethods;
    private final BuyingRequirementRepository buyingRequirementRepository;
    private static final Logger logger = LoggerFactory.getLogger(ListingServiceImpl.class);

    @Override
    public ListingResponseDto createListing(CreateListingRequest request,
            List<MultipartFile> imageFiles,
            MultipartFile certificateFile) {
        // validatorMethods.validateUserAccess(); //admin cant create
        logger.info("Creating listing...");
        ProductListingDto productDto = request.getProduct();
        QualityPricingListingDto pricingDto = request.getPricing();
        QualityLocationListingDto locationDto = request.getLocation();
        CropMaster crop = validatorMethods.validateCrop(Long.valueOf(productDto.getCropId()));
        UnitMaster unit = validatorMethods.validateUnit(Long.valueOf(pricingDto.getUnitId()));
        StateMaster state = validatorMethods.validateState(locationDto.getStateId());
        DistrictMaster district = validatorMethods.validateDistrict(locationDto.getDistrictId());
        PackagingMaster packaging = validatorMethods.validatePackaging(Long.valueOf(locationDto.getPackagingId()));
        StorageMaster storage = validatorMethods.validateStorage(Long.valueOf(locationDto.getStorageId()));

        validatePricing(pricingDto);

        logger.info("Mapping images and certificate...");

        logger.info("Creating listing for product: {}", productDto.getCropId());

        // Map images
        List<ListingImage> images = null;
        if (imageFiles != null && !imageFiles.isEmpty()) {
            images = imageFiles.stream().map(file -> {
                String path = fileStorageService.storeFile(file, "images");
                ListingImage image = new ListingImage();
                image.setFileName(file.getOriginalFilename());
                image.setFilePath(path);
                image.setFileType(file.getContentType());
                image.setIsPrimary(false);
                return image;
            }).collect(Collectors.toList());
            if (!images.isEmpty()) {
                images.get(0).setIsPrimary(true);
            }
        }

        // Map certificate
        ListingCertificate certificate = null;
        if (certificateFile != null && !certificateFile.isEmpty()) {
            String path = fileStorageService.storeFile(certificateFile, "certificates");
            certificate = new ListingCertificate();
            certificate.setFileName(certificateFile.getOriginalFilename());
            certificate.setFilePath(path);
            certificate.setFileType(certificateFile.getContentType());
        }

        String description = request.getDescription();

        Listing listing = ListingMapper.toEntity(productDto, pricingDto, locationDto, crop, unit, storage, packaging,
                state, district, images, certificate, description);
        listing.setState(state);
        listing.setDistrict(district);
        Long userId = validatorMethods.getCurrentUserId();
        User seller = validatorMethods.validateUserById(userId);
        listing.setSeller(seller);

        if (listing.getSaleType() == SaleType.AUCTION) {

            if (listing.getAuctionEndTime().isAfter(LocalDateTime.now())) {
                listing.setStatus(AuctionStatus.ACTIVE);
            } else {
                listing.setStatus(AuctionStatus.EXPIRED);
            }

        } else {
            listing.setStatus(AuctionStatus.ACTIVE);
        }

        BigDecimal totalBasePrice = pricingDto.getPricePerKg()
                .multiply(pricingDto.getQuantity());
        listing.setTotalBasePrice(totalBasePrice);

        Listing saved = listingRepository.save(listing);

        logger.info("Listing created successfully ID: {}", saved.getListingId());
        return ListingMapper.toResponse(saved,null);
    }

    @Override
    @CacheEvict(value = "listingDetails", key = "#listingId")
    public ListingResponseDto updateListing(Long listingId,
                                            CreateListingRequest request,
                                            List<MultipartFile> imageFiles,
                                            MultipartFile certificateFile) {

        logger.info("Updating listing...");
        Listing listing = validatorMethods.validateExists(listingId);

        ProductListingDto productDto = request.getProduct();
        QualityPricingListingDto pricingDto = request.getPricing();
        QualityLocationListingDto locationDto = request.getLocation();

        CropMaster crop = validatorMethods.validateCrop(Long.valueOf(productDto.getCropId()));
        UnitMaster unit = validatorMethods.validateUnit(Long.valueOf(pricingDto.getUnitId()));
        StateMaster state = validatorMethods.validateState(locationDto.getStateId());
        DistrictMaster district = validatorMethods.validateDistrict(locationDto.getDistrictId());
        PackagingMaster packaging = validatorMethods.validatePackaging(Long.valueOf(locationDto.getPackagingId()));
        StorageMaster storage = validatorMethods.validateStorage(Long.valueOf(locationDto.getStorageId()));

        validatePricing(pricingDto);

        String description = request.getDescription();

        ListingMapper.updateEntity(
                listing,
                productDto,
                pricingDto,
                locationDto,
                crop,
                unit,
                storage,
                packaging,
                state,
                district,
                description
        );

        if (imageFiles != null && !imageFiles.isEmpty()) {

            if (listing.getImages() != null && !listing.getImages().isEmpty()) {
                listing.getImages().forEach(img -> {
                    if (img.getFilePath() != null && !img.getFilePath().isBlank()) {
                        deletePhysicalFile(img.getFilePath());
                    }
                });
            }

            List<ListingImage> updatedImages = imageFiles.stream().map(file -> {
                String path = fileStorageService.storeFile(file, "images");
                ListingImage image = new ListingImage();
                image.setFileName(file.getOriginalFilename());
                image.setFilePath(path);
                image.setFileType(file.getContentType());
                image.setIsPrimary(false);
                return image;
            }).collect(Collectors.toList());

            if (!updatedImages.isEmpty()) {
                updatedImages.get(0).setIsPrimary(true);
            }

            listing.getImages().clear();
            listing.setImages(updatedImages);
        }

        if (certificateFile != null && !certificateFile.isEmpty()) {

            if (listing.getCertificate() != null &&
                    listing.getCertificate().getFilePath() != null &&
                    !listing.getCertificate().getFilePath().isBlank()) {

                deletePhysicalFile(listing.getCertificate().getFilePath());
            }

            String path = fileStorageService.storeFile(certificateFile, "certificates");

            ListingCertificate certificate = new ListingCertificate();
            certificate.setFileName(certificateFile.getOriginalFilename());
            certificate.setFilePath(path);
            certificate.setFileType(certificateFile.getContentType());

            listing.setCertificate(certificate);
        }

        Listing updated = listingRepository.save(listing);

        logger.info("Listing Updated Successfully ID: {}", updated.getListingId());

        return ListingMapper.toResponse(updated,null);
    }

    private void validatePricing(QualityPricingListingDto pricingDto) {
        logger.info("Validating pricing...");
        if (pricingDto.getQuantity() == null ||
                pricingDto.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new UserException("Quantity must be greater than 0", HttpStatus.BAD_REQUEST);
        }

        if (pricingDto.getPricePerKg() == null ||
                pricingDto.getPricePerKg().compareTo(BigDecimal.ZERO) <= 0) {
            throw new UserException("PricePerKg must be greater than 0", HttpStatus.BAD_REQUEST);
        }

        if (pricingDto.getSaleType() == null) {
            throw new UserException("SaleType must be specified", HttpStatus.BAD_REQUEST);
        }

        if (pricingDto.getSaleType() == SaleType.AUCTION) {

            if (pricingDto.getAuctionEndTime() == null) {
                throw new UserException("Auction End Time required", HttpStatus.BAD_REQUEST);
            }

            if (pricingDto.getAuctionEndTime().isBefore(LocalDateTime.now())) {
                throw new UserException("Auction has ended", HttpStatus.BAD_REQUEST);
            }

            if (pricingDto.getMinimumBidIncrement() == null ||
                    pricingDto.getMinimumBidIncrement()
                            .compareTo(BigDecimal.ZERO) <= 0) {

                throw new UserException("Minimum Bid Increment must be greater than 0", HttpStatus.BAD_REQUEST);
            }

            if (pricingDto.getMaximumBidIncrement() == null ||
                    pricingDto.getMaximumBidIncrement()
                            .compareTo(BigDecimal.ZERO) <= 0) {

                throw new UserException("Maximum Bid Increment must be greater than 0", HttpStatus.BAD_REQUEST);
            }

            if (pricingDto.getMaximumBidIncrement().compareTo(pricingDto.getMinimumBidIncrement()) < 0) {
                throw new UserException("Maximum Bid Increment must be greater than or equal to Minimum Bid Increment",
                        HttpStatus.BAD_REQUEST);
            }

            logger.info("Auction Listing validated");
        } else if (pricingDto.getSaleType() == SaleType.FIXED) {
            logger.info("Fixed Price Listing validated");
        } else {
            throw new UserException("SaleType must be AUCTION or FIXED", HttpStatus.BAD_REQUEST);
        }
        if (pricingDto.getPurchaseType() == PurchaseType.PARTIAL_ORDER_ALLOWS) {

            if (pricingDto.getMinimumOrderQuantity() == null ||
                    pricingDto.getMinimumOrderQuantity()
                            .compareTo(BigDecimal.ZERO) <= 0) {

                throw new UserException("Minimum Order Quantity must be greater than 0", HttpStatus.BAD_REQUEST);
            }

            if (pricingDto.getMoqPricePerKg() == null ||
                    pricingDto.getMoqPricePerKg()
                            .compareTo(BigDecimal.ZERO) <= 0) {

                throw new UserException("MOQ PricePerKg must be greater than 0", HttpStatus.BAD_REQUEST);
            }

            logger.info("Partial Order Listing validated");
        }
    }



    private void deletePhysicalFile(String filePath) {
        logger.info("Deleting file: {}", filePath);
        try {
            Path fullPath = Paths.get(uploadDir)
                    .toAbsolutePath()
                    .normalize()
                    .resolve(filePath);

            Files.deleteIfExists(fullPath);

        } catch (IOException e) {
            logger.error("Failed to delete file: {}", filePath, e);
        }
    }

    @Override
    public List<ListingResponseDto> getAllListings() {
        // validatorMethods.validateAdminAccess();
        logger.info("Getting all listings...");
        logger.info("Fetching all listings success...");
        List<Listing> listings = listingRepository.findAll();
        return listings.stream()
                .map(listing-> {
                    Optional<Bid> highestBid = bidRepository.findTopByListingListingIdOrderByBuyerAmountDesc(listing.getListingId());

                    return ListingMapper.toResponse(listing, highestBid.orElse(null)
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public ListingResponseDto getListingById(Long id) {
        // validatorMethods.validateUserAccess();
        logger.info("Get listing by id...");
        logger.info("Validating listing...");
        Listing listing = validatorMethods.validateExists(id);
        logger.info("Fetching listing by id success...");
        return ListingMapper.toResponse(listing,null);
    }

    @Override
    public void deleteListing(Long listingId, Long sellerId) {

        logger.info("Deleting listing with ID: {}", listingId);
        Listing listing = validatorMethods.validateExists(listingId);


        if (listing.getStatus() == AuctionStatus.SOLD) {
            throw new UserException("Cannot delete a SOLD listing", HttpStatus.BAD_REQUEST);
        }

        if (listing.getImages() != null && !listing.getImages().isEmpty()) {
            listing.getImages().forEach(image -> {
                if (image.getFilePath() != null && !image.getFilePath().isBlank()) {
                    deletePhysicalFile(image.getFilePath());
                }
            });
        }

        if (listing.getCertificate() != null &&
                listing.getCertificate().getFilePath() != null &&
                !listing.getCertificate().getFilePath().isBlank()) {

            deletePhysicalFile(listing.getCertificate().getFilePath());
        }

        listingRepository.delete(listing);

        logger.info("Listing deleted successfully ID: {}", listingId);
    }


    @Override
    public SellerListingDto getSellerAuctionListingDetail(Long listingId) {

        logger.info("Getting seller auction listing detail...");

        Listing listing = validatorMethods.validateExists(listingId);

        if (listing.getSaleType() != SaleType.AUCTION) {
            throw new RuntimeException("Not an auction listing");
        }

        BigDecimal currentHighestBid = bidRepository.findTopByListingListingIdOrderByBuyerAmountDesc
                        (listingId)
                .map(Bid::getBuyerAmount)
                .orElse(listing.getTotalBasePrice());

        List<BidResponseDto> top5Bids = bidRepository
                .findTop5ByListingListingIdAndBidStatusInOrderByBuyerAmountDesc
                        (listingId,List.of(BidStatus.PENDING,BidStatus.OUTBID,BidStatus.ACCEPTED)) //
                .stream()
                .map(bid -> new BidResponseDto(
                        bid.getBidId(),
                        bid.getBuyer().getUserId(),
                        bid.getBuyerAmount(),
                        bid.getBuyer().getFullName(),
                        bid.getBidTime(),
                        bid.getBidStatus(), //
                        bid.getListing().getSeller().getUserId()
                ))
                .toList();
        long totalBids = bidRepository.countTotalBidsBySellerId(listingId);
        long activeBidders = bidRepository.countActiveBidders(listingId);

        return new SellerListingDto(
                listing.getListingId(),
                listing.getCrop().getCropName(),
                listing.getVariety(),
                listing.getGrade(),
                listing.getQuantity(),
                listing.getPricePerKg(),
                listing.getUnit().getUnitName(),
                listing.getTotalBasePrice(),
                listing.getMinimumBidIncrement(),
                listing.getMaximumBidIncrement(),
                totalBids,
                activeBidders,
                listing.getStatus(),
                listing.getAuctionEndTime(),
                currentHighestBid,
                listing.getPurchaseType(),
                listing.getSaleType(),
                listing.getPostedOn(),
                listing.getState().getName(),
                listing.getDistrict().getName(),
                listing.getPickupMethod(),
                listing.getStorage().getStorageType(),
                top5Bids,
                null,
                ListingMapper.mapImages(listing),
                listing.getHarvestDate(),
                listing.getPackaging().getPackagingType(),
                listing.getSeller().getUserId());
    }

    @Override
    public SellerListingFixedDto getSellerFixedListingDetail(Long listingId) {

        logger.info("Getting seller fixed listing detail...");

        Listing listing = validatorMethods.validateExists(listingId);

        if (listing.getSaleType() != SaleType.FIXED) {
            throw new IllegalArgumentException("Not a fixed listing");
        }

        return new SellerListingFixedDto(

                listing.getListingId(),

                listing.getCrop().getCropName(),
                listing.getVariety(),
                listing.getGrade(),

                listing.getHarvestDate(),

                listing.getQuantity(),

                listing.getPackaging().getPackagingType(),
                listing.getStorage().getStorageType(),

                listing.getState().getName(),
                listing.getDistrict().getName(),

                listing.getPickupMethod(),

                listing.getTotalBasePrice(),
                listing.getPricePerKg(),

                listing.getMoqPricePerKg(),

                listing.getPostedOn(),

                ListingMapper.mapImages(listing),

                listing.getDescription(),

                listing.getSaleType(),
                listing.getPurchaseType());
    }

    @Override
    public DashboardDto getSellerOverview() {
        logger.info("Getting seller overview...");
        Long userId = validatorMethods.getCurrentUserId();
        User seller = validatorMethods.validateUserById(userId);

        Long sellerId = seller.getUserId();
        Long activeListings = listingRepository.countBySeller_UserIdAndStatus(sellerId, AuctionStatus.ACTIVE);
        Long pendingApprovals = listingRepository.countBySeller_UserIdAndStatus(sellerId, AuctionStatus.PENDING);
        Long totalBidsReceived = bidRepository.countTotalBidsBySellerId(sellerId);
        BigDecimal totalRevenue = bidRepository.sumAmountByListingSellerId(sellerId);
        logger.info("Fetching seller overview success...");
        return new DashboardDto(activeListings, pendingApprovals, totalBidsReceived, totalRevenue);
    }

    @Override
    public Page<ListingResponseDto> myListings(Long userId, Pageable pageable) {
        logger.info("Getting all my listings...");
        Page<Listing> listings = listingRepository.findBySeller_UserId(userId, pageable);
        logger.info("Fetching my listings success...");
        return listings.map(listing -> {
            ListingResponseDto dto = ListingMapper.toResponse(listing,null);

            // Fetch top bid for this listing
            Bid topBid = bidRepository.findTopByListingListingIdAndBidStatusOrderByBuyerAmountDesc(
                    listing.getListingId(), BidStatus.PENDING).orElse(null);

            if (topBid != null) {
                dto.setTopBid(topBid.getBidId());
                dto.setHighestBid(topBid.getBuyerAmount());
                dto.setTopBidderName(topBid.getBuyer().getFullName());
                dto.setTopBid(topBid.getBidId());
            }

            return dto;
        });
    }

    @Override
    public Page<ListingResponseDto> activeListings(Long sellerId, Pageable pageable) {
        logger.info("Getting active listings...");
        Page<Listing> listings = listingRepository.findBySeller_UserIdAndStatus(sellerId, AuctionStatus.ACTIVE,
                pageable);
        return listings.map(listing -> {

            Optional<Bid> highestBidOpt =
                    bidRepository.findTopByListingListingIdOrderByBuyerAmountDesc(
                            listing.getListingId()
                    );

            return ListingMapper.toResponse(listing, highestBidOpt.orElse(null));
        });
    }

    @Override
    public Page<Object> activeSummaryListings(Long sellerId, Pageable pageable, String cropName) {

        logger.info("Getting active listings...");

        Page<Listing> listings;
        if (cropName != null && !cropName.isBlank()) {
            listings = listingRepository.findBySeller_UserIdAndStatusAndCrop_CropNameContainingIgnoreCase(
                    sellerId, AuctionStatus.ACTIVE, cropName.trim(), pageable);
        } else {
            listings = listingRepository.findBySeller_UserIdAndStatus(
                    sellerId, AuctionStatus.ACTIVE, pageable);
        }

        return listings.map(listing -> {
            Bid nextTopBid = bidRepository
                    .findTopByListingListingIdAndBidStatusOrderByBuyerAmountDesc(
                            listing.getListingId(),
                            BidStatus.PENDING)
                    .orElse(null);
            Long topBidId = nextTopBid != null ? nextTopBid.getBidId() : null;
            if (listing.getSaleType() == SaleType.AUCTION) {

                AuctionListingResponseDto dto = ListingMapper.toAuctionListingResponseDto(listing, topBidId);

                bidRepository
                        .findTopByListingListingIdAndBidStatusOrderByBuyerAmountDesc(
                                listing.getListingId(),
                                BidStatus.PENDING)
                        .ifPresent(bid -> {
                            dto.setHighestBid(bid.getBuyerAmount());
                            dto.setTopBidderName(bid.getBuyer().getFullName());
                        });

                return dto;

            } else {
                return ListingMapper.toFixedResponseDto(listing, topBidId);
            }

        });
    }

    @Override
    public Page<ListingResponseDto> pendingListings(Long sellerId, Pageable pageable) {
        logger.info("Getting pending listings...");
        Page<Listing> listings = listingRepository.findBySeller_UserIdAndStatus(sellerId, AuctionStatus.PENDING,
                pageable);
        logger.info("Fetching pending listings success...");
        return listings.map(listing -> {

            Optional<Bid> highestBidOpt =
                    bidRepository.findTopByListingListingIdOrderByBuyerAmountDesc(
                            listing.getListingId()
                    );

            return ListingMapper.toResponse(listing, highestBidOpt.orElse(null));
        });
    }

    @Override
    public Page<ListingResponseDto> soldListings(Long sellerId, Pageable pageable) {
        logger.info("Getting sold closed listings...");
        Page<Listing> listings = listingRepository.findBySeller_UserIdAndStatus(sellerId, AuctionStatus.SOLD, pageable);
        logger.info("Fetching sold listing success...");
        return listings.map(listing -> {

            Optional<Bid> highestBidOpt =
                    bidRepository.findTopByListingListingIdOrderByBuyerAmountDesc(
                            listing.getListingId()
                    );

            return ListingMapper.toResponse(listing, highestBidOpt.orElse(null));
        });
    }

    @Override
    public Page<ListingResponseDto> closedListings(Long sellerId, Pageable pageable) {
        logger.info("Getting closed listings...");
        Page<Listing> listings = listingRepository.findBySeller_UserIdAndStatus(sellerId, AuctionStatus.EXPIRED,
                pageable);
        logger.info("Fetching closed listings success...");
        return listings.map(listing -> {

            Optional<Bid> highestBidOpt =
                    bidRepository.findTopByListingListingIdOrderByBuyerAmountDesc(
                            listing.getListingId()
                    );

            return ListingMapper.toResponse(listing, highestBidOpt.orElse(null));
        });
    }

    @Override
    public String extendAuctionTime(ExtendAuctionDto dto) {

        logger.info("Extending auction time...");

        Listing listing = listingRepository.findById(dto.getListingId())
                .orElseThrow(() -> new UserException("Listing not found", HttpStatus.NOT_FOUND));

        // Check seller ownership
        if (!listing.getSeller().getUserId().equals(dto.getSellerId())) {
            logger.error("Unauthorized auction extension attempt...");
            throw new UserException("You are not authorized to extend this auction", HttpStatus.FORBIDDEN);
        }

        if (listing.getStatus() != AuctionStatus.ACTIVE) {
            throw new UserException("Auction is not active", HttpStatus.BAD_REQUEST);
        }

        if (listing.getAuctionEndTime().isBefore(LocalDateTime.now())) {
            throw new UserException("Cannot extend expired auction", HttpStatus.BAD_REQUEST);
        }

        if (dto.getMinutes() == null || dto.getMinutes() <= 0) {
            throw new UserException("Extension time must be greater than 0", HttpStatus.BAD_REQUEST);
        }

        listing.setAuctionEndTime(
                listing.getAuctionEndTime().plusMinutes(dto.getMinutes()));

        listingRepository.save(listing);

        logger.info("Auction time extended successfully for listingId: {}", dto.getListingId());

        return "Auction time extended successfully";
    }

    @Override
    public List<BuyingRequirementResponseDto> getAllRequirementsForSeller(String cropName) {

        Long currentUserId = validatorMethods.getCurrentUserId();

        List<BuyingRequirement> list;
        if (cropName != null && !cropName.isBlank()) {
            list = buyingRequirementRepository
                    .findByRequirementStatusAndBuyer_UserIdNotAndCrop_CropNameContainingIgnoreCase(
                            RequirementStatus.OPEN, currentUserId, cropName.trim());
        } else {
            list = buyingRequirementRepository
                    .findByRequirementStatusAndBuyer_UserIdNot(
                            RequirementStatus.OPEN, currentUserId);
        }

        return list.stream()
                .map(BuyingRequirementMapper::toDto)
                .toList();
    }

    @Override
    public BuyerContactResponseDto getBuyerContact(Long requirementId) {
        // validatorMethods.validateUserAccess();
        logger.info("Getting buyer contact...");
        BuyingRequirement requirement = buyingRequirementRepository.findById(requirementId)
                .orElseThrow(() -> new RuntimeException("Requirement not found"));

        User buyer = requirement.getBuyer();
        logger.info("Fetching buyer contact success...");
        return new BuyerContactResponseDto(
                requirement.getRequirementId(),
                buyer.getFullName(),
                buyer.getMobileNumber(),
                requirement.getCrop().getCropName(),
                requirement.getQuantityRequired(),
                requirement.getUnit().getUnitName());
    }

    @Override
    public List<RecentBidResponseDto> getRecentBids() {
        logger.info("Getting recent bids...");
        logger.info("Validating user...");
        Long userId = validatorMethods.getCurrentUserId();
        User seller = validatorMethods.validateUserById(userId);

        List<Bid> bids = bidRepository
                .findTop5ByListingSellerUserIdAndBidStatusOrderByCreatedAtDesc(
                        seller.getUserId(), BidStatus.PENDING);

        List<RecentBidResponseDto> response = new ArrayList<>();

        for (Bid bid : bids) {
            response.add(RecentBidResponseDto.builder()
                    .bidderId(bid.getBidId())
                    .bidderName(bid.getBuyer().getFullName())
                    .cropListing(bid.getListing().getCrop().getCropName())
                    .bidAmount(bid.getBuyerAmount())
                    .timestamp(bid.getCreatedAt())
                    .bidStatus(bid.getBidStatus().name())
                    .build());
        }
        logger.info("Fetching recent bids success...");
        return response;
    }

    // @Override
    // public Page<ListingSummaryResponseDto> activeSummaryListings(Long sellerId,
    // Pageable pageable) {
    // logger.info("Getting active listings...");
    // Page<Listing> listings =
    // listingRepository.findBySeller_UserIdAndStatus(sellerId,
    // AuctionStatus.ACTIVE, pageable);
    // logger.info("Fetching active listings success...");
    // return listings.map(listing -> {
    // ListingSummaryResponseDto dto = ListingMapper.toSummaryResponse(listing);
    // dto.setCurrentHighestBid(resolveCurrentHighestBid(listing));
    // return dto;
    // });
    // }

    @Override
    public Page<ListingSummaryResponseDto> pendingSummaryListings(Long sellerId, Pageable pageable) {
        logger.info("Getting pending listings...");
        Page<Listing> listings = listingRepository.findBySeller_UserIdAndStatus(sellerId, AuctionStatus.PENDING,
                pageable);
        logger.info("Fetching pending listings success...");
        return listings.map(listing -> {
            ListingSummaryResponseDto dto = ListingMapper.toSummaryResponse(listing);
            dto.setCurrentHighestBid(resolveCurrentHighestBid(listing));
            return dto;
        });
    }

    @Override
    public Page<ListingSummaryResponseDto> soldSummaryListings(Long sellerId, Pageable pageable) {
        logger.info("Getting sold closed listings...");
        Page<Listing> listings = listingRepository.findBySeller_UserIdAndStatus(sellerId, AuctionStatus.SOLD, pageable);
        logger.info("Fetching sold listing success...");
        return listings.map(listing -> {
            ListingSummaryResponseDto dto = ListingMapper.toSummaryResponse(listing);
            dto.setCurrentHighestBid(resolveCurrentHighestBid(listing));
            return dto;
        });
    }

    @Override
    public Page<ListingSummaryResponseDto> closedSummaryListings(Long sellerId, Pageable pageable) {
        logger.info("Getting closed listings...");
        Page<Listing> listings = listingRepository.findBySeller_UserIdAndStatus(sellerId, AuctionStatus.EXPIRED,
                pageable);
        logger.info("Fetching closed listings success...");
        return listings.map(listing -> {
            ListingSummaryResponseDto dto = ListingMapper.toSummaryResponse(listing);
            dto.setCurrentHighestBid(resolveCurrentHighestBid(listing));
            return dto;
        });
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
