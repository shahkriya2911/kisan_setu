package com.project.kisan_setu.service.impl;
import com.project.kisan_setu.dto.ResponseDto.CommodityListingDto;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.Order;
import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.repository.BidRepository;
import com.project.kisan_setu.repository.ListingRepository;
import com.project.kisan_setu.repository.OrderRepository;
import com.project.kisan_setu.service.AdminCommodityService;
import com.project.kisan_setu.util.ValidatorMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCommodityServiceImpl implements AdminCommodityService {
    private final ListingRepository listingRepository;
    private final BidRepository bidRepository;
    private final OrderRepository orderRepository;
    private final ValidatorMethods validatorMethods;

    private CommodityListingDto mapToCommodityListingDto(Listing listing) {
        return new CommodityListingDto(
                listing.getListingId(),
                listing.getCrop() != null ? listing.getCrop().getCropName() : null,
                listing.getSeller() != null ? listing.getSeller().getFullName() : null,
                listing.getQuantity(),
                listing.getUnit() != null ? listing.getUnit().getUnitName() : null,
                listing.getTotalBasePrice(),
                getHighestBid(listing.getListingId()),
                getOrderStatus(listing.getListingId()),
                listing.getPostedOn()
        );
    }

    private String getOrderStatus(Long listingId) {
        Order order = orderRepository.findByListingListingId(listingId);
        if(order == null || order.getStatus()==null)
            return "PENDING";
        return order.getStatus().name();
    }

    private BigDecimal getHighestBid(Long listingId) {
        BigDecimal highestBid = bidRepository.getHighestBid(listingId);
        return highestBid != null ? highestBid : BigDecimal.ZERO;
    }

    @Override
    public List<CommodityListingDto> getAllCommodityListings() {
        validatorMethods.validateAdminAccess();
        return listingRepository.findAll().stream()
                .map(this::mapToCommodityListingDto)
                .toList();
    }

    @Override
    public List<CommodityListingDto> getActiveCommodityListings() {
        validatorMethods.validateAdminAccess();
        return listingRepository.findAll().stream()
                .filter(listing -> "ACTIVE".equalsIgnoreCase(getOrderStatus(listing.getListingId())))
                .map(this::mapToCommodityListingDto)
                .toList();
    }
    @Override
    public List<CommodityListingDto> getPendingCommodityListings() {
        validatorMethods.validateAdminAccess();
        return listingRepository.findAll().stream()
                .filter(listing -> "PENDING".equalsIgnoreCase(getOrderStatus(listing.getListingId())))
                .map(this::mapToCommodityListingDto)
                .toList();
    }

    @Override
    public List<CommodityListingDto> getCompletedCommodityListings() {
        validatorMethods.validateAdminAccess();
        return listingRepository.findAll().stream()
                .filter(listing -> "COMPLETED".equalsIgnoreCase(getOrderStatus(listing.getListingId())))
                .map(this::mapToCommodityListingDto)
                .toList();
    }

    @Override
    public String rejectListing(Long listingId) {

        validatorMethods.validateAdminAccess();

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (listing.getStatus() == AuctionStatus.REJECTED) {
            return "Listing already rejected";
        }

        listing.setStatus(AuctionStatus.REJECTED);

        listingRepository.save(listing);

        return "Listing rejected successfully";
    }

}
