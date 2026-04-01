package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.entity.CropMaster;
import com.project.kisan_setu.entity.DistrictMaster;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.Order;
import com.project.kisan_setu.entity.StateMaster;
import com.project.kisan_setu.entity.UnitMaster;
import com.project.kisan_setu.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InvoiceServiceImplTest {

    private InvoiceServiceImpl invoiceService;

    @BeforeEach
    void setUp() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        templateResolver.setCacheable(false);

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        invoiceService = new InvoiceServiceImpl(templateEngine);
    }

    @Test
    void generateInvoiceReturnsRealPdfBytes() {
        Order order = new Order();
        order.setOrderId(23L);
        order.setAmount(new BigDecimal("52000"));
        order.setQuantity(new BigDecimal("520"));
        order.setPricePerKg(new BigDecimal("100"));
        order.setCreatedAt(LocalDateTime.now().minusDays(1));
        order.setCompletedAt(LocalDateTime.now());

        User buyer = new User();
        buyer.setFullName("Suresh Singh");
        order.setBuyer(buyer);

        User seller = new User();
        seller.setFullName("Mahesh Agro");
        order.setSeller(seller);

        CropMaster crop = new CropMaster();
        crop.setCropName("Wheat");

        UnitMaster unit = new UnitMaster();
        unit.setUnitName("Kg");

        StateMaster state = new StateMaster();
        state.setName("Maharashtra");

        DistrictMaster district = new DistrictMaster();
        district.setName("Nashik");
        district.setState(state);

        Listing listing = new Listing();
        listing.setCrop(crop);
        listing.setVariety("Sharbati");
        listing.setUnit(unit);
        listing.setState(state);
        listing.setDistrict(district);
        order.setListing(listing);

        byte[] pdf = invoiceService.generateInvoice(order);
        String header = new String(pdf, 0, Math.min(pdf.length, 5), StandardCharsets.US_ASCII);

        assertTrue(pdf.length > 0);
        assertTrue(header.startsWith("%PDF-"));
    }
}
