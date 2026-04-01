package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.Order;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.enums.EscrowStatus;
import com.project.kisan_setu.enums.NotificationStatus;
import com.project.kisan_setu.enums.OrderStatus;
import com.project.kisan_setu.repository.BidRepository;
import com.project.kisan_setu.repository.ListingRepository;
import com.project.kisan_setu.repository.OrderRepository;
import com.project.kisan_setu.service.NotificationService;
import com.project.kisan_setu.util.OtpGenerator;
import com.project.kisan_setu.util.ValidatorMethods;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private BidRepository bidRepository;

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private ValidatorMethods validatorMethods;

    @Mock
    private NotificationService notificationService;

    @Mock
    private OtpGenerator otpGenerator;

    @Test
    void verifyDeliveryOtpCompletesOrderAndMarksInvoiceReady() {
        OrderServiceImpl orderService = new OrderServiceImpl(
                orderRepository,
                bidRepository,
                listingRepository,
                validatorMethods,
                notificationService,
                otpGenerator
        );

        User seller = new User();
        seller.setUserId(7L);
        seller.setFullName("Mahesh Agro");

        User buyer = new User();
        buyer.setUserId(9L);
        buyer.setFullName("Suresh Singh");

        Listing listing = new Listing();
        listing.setListingId(14L);
        listing.setSeller(seller);

        Order order = new Order();
        order.setOrderId(21L);
        order.setSeller(seller);
        order.setBuyer(buyer);
        order.setListing(listing);
        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        order.setEscrowStatus(EscrowStatus.HELD);
        order.setDeliveryOtp("654321");
        order.setOtpGeneratedAt(LocalDateTime.now().minusHours(1));
        order.setOtpVerified(false);

        when(validatorMethods.getCurrentUserId()).thenReturn(7L);
        when(orderRepository.findById(21L)).thenReturn(Optional.of(order));

        String response = orderService.verifyDeliveryOtp(21L, "654321");

        assertEquals(OrderStatus.COMPLETED, order.getStatus());
        assertEquals(EscrowStatus.RELEASED, order.getEscrowStatus());
        assertTrue(order.isOtpVerified());
        assertNotNull(order.getCompletedAt());
        assertEquals("Delivery confirmed, payment released, invoice ready for download.", response);

        verify(orderRepository).save(order);
        verify(notificationService).createNotification(
                eq(seller),
                contains("Payment released"),
                eq(NotificationStatus.PAYMENT_RELEASED),
                eq(listing),
                isNull(),
                eq(order)
        );
    }
}
