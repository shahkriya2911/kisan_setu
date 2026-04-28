package com.project.kisan_setu.mapper;
import com.project.kisan_setu.dto.ResponseDto.NotificationResponseDto;
import com.project.kisan_setu.entity.Bid;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.Notification;
import com.project.kisan_setu.entity.Order;
import com.project.kisan_setu.enums.NotificationStatus;

import static com.project.kisan_setu.mapper.ListingMapper.mapImages;

public class NotificationMapper {

    public static NotificationResponseDto toDto(Notification n){

        NotificationResponseDto dto = new NotificationResponseDto();

        dto.setNotificationId(n.getNotificationId());
        dto.setMessage(n.getMessage());
        dto.setIsRead(n.getIsRead());
        dto.setType(n.getType());
        dto.setActionCompleted(n.getActionCompleted());

        // Listing details
        if (n.getListing() != null){
            Listing listing = n.getListing();

            dto.setListingId(listing.getListingId());

            if (listing.getCrop() != null){
                dto.setCropName(listing.getCrop().getCropName());
            }
            if (n.getType() == NotificationStatus.BID_ACCEPTED){
                dto.setPricePerKg(listing.getPricePerKg());
            }

            dto.setVariety(listing.getVariety());
            dto.setQuantity(listing.getQuantity());
            dto.setUnit(listing.getUnit().getUnitName());

            if (listing.getSeller() != null) {
                dto.setSellerName(listing.getSeller().getFullName());
            }
        }

        // Bid details
        if (n.getBid() != null){
            Bid bid = n.getBid();

            dto.setBidId(bid.getBidId());
            dto.setBidAmount(bid.getBuyerAmount());
        }

        // Order details
        if (n.getOrder() != null){
            Order order = n.getOrder();
            dto.setOrderId(order.getOrderId());
        }
        return dto;
    }

}