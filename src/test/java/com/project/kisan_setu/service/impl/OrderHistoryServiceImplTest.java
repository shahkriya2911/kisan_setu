package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.entity.Order;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.enums.OrderStatus;
import com.project.kisan_setu.repository.OrderRepository;
import com.project.kisan_setu.repository.RatingReviewRepository;
import com.project.kisan_setu.repository.ReportUserRepository;
import com.project.kisan_setu.service.InvoiceService;
import com.project.kisan_setu.util.ValidatorMethods;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderHistoryServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ValidatorMethods validatorMethods;

    @Mock
    private RatingReviewRepository ratingReviewRepository;

    @Mock
    private ReportUserRepository reportUserRepository;

    @Mock
    private InvoiceService invoiceService;

    private OrderHistoryServiceImpl orderHistoryService;

    @BeforeEach
    void setUp() {
        orderHistoryService = new OrderHistoryServiceImpl(
                orderRepository,
                validatorMethods,
                ratingReviewRepository,
                reportUserRepository,
                invoiceService
        );
    }

    @Test
    void downloadInvoiceAllowsAuthorizedBuyer() {
        Order order = createCompletedOrder(4L, 8L);
        byte[] expectedPdf = "%PDF-test".getBytes();

        when(validatorMethods.getCurrentUserId()).thenReturn(4L);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(invoiceService.generateInvoice(order)).thenReturn(expectedPdf);

        byte[] actualPdf = orderHistoryService.downloadInvoice(10L);

        assertArrayEquals(expectedPdf, actualPdf);
        verify(invoiceService).generateInvoice(order);
    }

    @Test
    void downloadInvoiceRejectsUnrelatedUser() {
        Order order = createCompletedOrder(4L, 8L);

        when(validatorMethods.getCurrentUserId()).thenReturn(99L);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderHistoryService.downloadInvoice(10L)
        );

        assertEquals("Unauthorized: You cannot access this invoice", exception.getMessage());
    }

    private Order createCompletedOrder(Long buyerId, Long sellerId) {
        User buyer = new User();
        buyer.setUserId(buyerId);

        User seller = new User();
        seller.setUserId(sellerId);

        Order order = new Order();
        order.setOrderId(10L);
        order.setBuyer(buyer);
        order.setSeller(seller);
        order.setStatus(OrderStatus.COMPLETED);
        return order;
    }
}
