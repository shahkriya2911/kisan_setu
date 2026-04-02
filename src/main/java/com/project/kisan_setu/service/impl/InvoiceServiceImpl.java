package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.Order;
import com.project.kisan_setu.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private static final Locale INDIA_LOCALE = Locale.forLanguageTag("en-IN");
    private static final DateTimeFormatter INVOICE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH);

    private final TemplateEngine templateEngine;

    @Override
    public byte[] generateInvoice(Order order) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            String renderedHtml = renderInvoiceHtml(order);

            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(renderedHtml);
            renderer.layout();
            renderer.createPDF(outputStream);

            return outputStream.toByteArray();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to generate invoice PDF", exception);
        }
    }

    String renderInvoiceHtml(Order order) {
        Context context = new Context();
        populateInvoiceContext(context, order);
        return templateEngine.process("invoice", context);
    }

    private void populateInvoiceContext(Context context, Order order) {
        Listing listing = order.getListing();
        LocalDateTime invoiceDate = order.getCompletedAt() != null ? order.getCompletedAt() : order.getCreatedAt();

        context.setVariable("amount", formatCurrency(order.getAmount()));
        context.setVariable(
                "buyerName",
                order.getBuyer() != null ? defaultText(order.getBuyer().getFullName(), "N/A") : "N/A"
        );
        context.setVariable(
                "sellerName",
                order.getSeller() != null ? defaultText(order.getSeller().getFullName(), "N/A") : "N/A"
        );
        context.setVariable("invoiceDate", invoiceDate != null ? invoiceDate.format(INVOICE_DATE_FORMAT) : "N/A");
        context.setVariable("orderReference", formatOrderReference(order.getOrderId(), invoiceDate));
        context.setVariable("paymentMode", "Escrow");
        context.setVariable("statusLabel", "VERIFIED");
        context.setVariable(
                "cropName",
                listing != null && listing.getCrop() != null ? defaultText(listing.getCrop().getCropName(), "-") : "-"
        );
        context.setVariable("variety", listing != null ? defaultText(listing.getVariety(), "-") : "-");
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }
        NumberFormat currencyFormatter = NumberFormat.getNumberInstance(INDIA_LOCALE);
        currencyFormatter.setMinimumFractionDigits(0);
        currencyFormatter.setMaximumFractionDigits(2);
        return currencyFormatter.format(amount);
    }

    private String formatOrderReference(Long orderId, LocalDateTime invoiceDate) {
        int year = invoiceDate != null ? invoiceDate.getYear() : LocalDateTime.now().getYear();
        long safeOrderId = orderId != null ? orderId : 0L;
        return String.format("#ORD-%d-%06d", year, safeOrderId);
    }

    private String defaultText(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }
}
