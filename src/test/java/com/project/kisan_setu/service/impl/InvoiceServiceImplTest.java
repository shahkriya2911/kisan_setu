package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.entity.CropMaster;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.Order;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.enums.EscrowStatus;
import com.project.kisan_setu.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InvoiceServiceImplTest {

    private InvoiceServiceImpl invoiceService;

    @BeforeEach
    void setUp() {
        invoiceService = new InvoiceServiceImpl(templateEngine());
    }

    @Test
    void generateInvoiceReturnsRealPdfBytes() {
        Order order = buildOrder(
                23L,
                "52000",
                "Suresh Singh",
                "Mahesh Agro",
                "Wheat",
                "Sharbati",
                LocalDateTime.now()
        );
        byte[] pdf = invoiceService.generateInvoice(order);
        String header = new String(pdf, 0, Math.min(pdf.length, 5), StandardCharsets.US_ASCII);

        assertTrue(pdf.length > 0);
        assertTrue(header.startsWith("%PDF-"));
    }

    @Test
    void generateInvoiceFallsBackToDirectPdfWhenHtmlRenderingFails() {
        invoiceService = new InvoiceServiceImpl(templateEngine()) {
            @Override
            String renderInvoiceHtml(Order order) {
                throw new RuntimeException("broken html");
            }
        };

        Order order = buildOrder(
                29L,
                "14500",
                "Mehul Shah",
                "Kisan Agro",
                "Wheat",
                "Lokwan",
                LocalDateTime.now()
        );

        byte[] pdf = invoiceService.generateInvoice(order);
        String header = new String(pdf, 0, Math.min(pdf.length, 5), StandardCharsets.US_ASCII);

        assertTrue(pdf.length > 0);
        assertTrue(header.startsWith("%PDF-"));
    }

    @Test
    void renderInvoiceHtmlUsesActualOrderValues() {
        Order firstOrder = buildOrder(
                23L,
                "52000",
                "Asha Traders",
                "Green Farms",
                "Wheat",
                "Sharbati",
                LocalDateTime.of(2026, 3, 10, 11, 0)
        );
        Order secondOrder = buildOrder(
                24L,
                "18650",
                "Ravi Exports",
                "Sunrise Agro",
                "Maize",
                "Sweet Corn",
                LocalDateTime.of(2026, 3, 11, 12, 30)
        );
        firstOrder.setStatus(OrderStatus.COMPLETED);
        secondOrder.setStatus(OrderStatus.OUT_FOR_DELIVERY);

        String firstHtml = invoiceService.renderInvoiceHtml(firstOrder);
        String secondHtml = invoiceService.renderInvoiceHtml(secondOrder);

        assertTrue(firstHtml.contains("invoice-logo.png"));
        assertTrue(firstHtml.contains("invoice-tick.png"));
        assertTrue(firstHtml.contains("Asha Traders"));
        assertTrue(firstHtml.contains("Green Farms"));
        assertTrue(firstHtml.contains("Wheat"));
        assertTrue(firstHtml.contains("Sharbati"));
        assertTrue(firstHtml.contains("52,000"));
        assertTrue(firstHtml.contains("23"));
        assertFalse(firstHtml.contains("#ORD"));

        assertTrue(secondHtml.contains("Ravi Exports"));
        assertTrue(secondHtml.contains("Sunrise Agro"));
        assertTrue(secondHtml.contains("Maize"));
        assertTrue(secondHtml.contains("Sweet Corn"));
        assertTrue(secondHtml.contains("18,650"));
        assertTrue(secondHtml.contains("24"));
        assertFalse(secondHtml.contains("#ORD"));
        assertFalse(secondHtml.contains("Suresh Singh"));
        assertFalse(secondHtml.contains("Mahesh Agro"));

        assertTrue(firstHtml.contains("Escrow"));
        assertTrue(secondHtml.contains("Escrow"));
        assertTrue(firstHtml.contains("VERIFIED"));
        assertTrue(secondHtml.contains("OUT FOR DELIVERY"));
        assertNotEquals(firstHtml, secondHtml);
    }

    @Test
    void renderInvoiceHtmlFallsBackForMissingOptionalFields() {
        Order order = buildOrder(
                25L,
                "0",
                null,
                null,
                null,
                null,
                LocalDateTime.of(2026, 3, 12, 9, 15)
        );

        String html = invoiceService.renderInvoiceHtml(order);

        assertTrue(html.contains("N/A"));
        assertFalse(html.contains("null"));
    }

    @Test
    void renderInvoiceHtmlFallsBackToListingSellerWhenOrderSellerMissing() {
        Order order = buildOrder(
                26L,
                "8750",
                "Buyer One",
                "Primary Seller",
                "Bajra",
                "Pearl",
                LocalDateTime.of(2026, 3, 13, 14, 0)
        );
        order.setSeller(null);

        User listingSeller = new User();
        listingSeller.setFullName("Listing Seller");
        order.getListing().setSeller(listingSeller);

        String html = invoiceService.renderInvoiceHtml(order);

        assertTrue(html.contains("Listing Seller"));
        assertFalse(html.contains("Primary Seller"));
    }

    @Test
    void renderInvoiceHtmlUsesVerifiedStatusForReleasedEscrow() {
        Order order = buildOrder(
                27L,
                "9150",
                "Buyer Two",
                "Seller Two",
                "Soybean",
                "JS 335",
                LocalDateTime.of(2026, 3, 14, 10, 0)
        );
        order.setStatus(null);
        order.setEscrowStatus(EscrowStatus.RELEASED);

        String html = invoiceService.renderInvoiceHtml(order);

        assertTrue(html.contains("VERIFIED"));
        assertFalse(html.contains("RELEASED"));
    }

    private SpringTemplateEngine templateEngine() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        templateResolver.setCacheable(false);

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }

    private Order buildOrder(Long orderId,
                             String amount,
                             String buyerName,
                             String sellerName,
                             String cropName,
                             String variety,
                             LocalDateTime completedAt) {
        Order order = new Order();
        order.setOrderId(orderId);
        order.setAmount(new BigDecimal(amount));
        order.setCreatedAt(completedAt.minusDays(1));
        order.setCompletedAt(completedAt);

        User buyer = new User();
        buyer.setFullName(buyerName);
        order.setBuyer(buyer);

        User seller = new User();
        seller.setFullName(sellerName);
        order.setSeller(seller);

        Listing listing = new Listing();
        if (cropName != null) {
            CropMaster crop = new CropMaster();
            crop.setCropName(cropName);
            listing.setCrop(crop);
        }
        listing.setSeller(seller);
        listing.setVariety(variety);
        order.setListing(listing);
        return order;
    }
}
