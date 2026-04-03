package com.project.kisan_setu.service.impl;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.Order;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.awt.Color;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceServiceImpl.class);
    private static final Locale INDIA_LOCALE = Locale.forLanguageTag("en-IN");
    private static final String PAYMENT_MODE = "Escrow";
    private static final DateTimeFormatter INVOICE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH);

    private final TemplateEngine templateEngine;

    @Override
    public byte[] generateInvoice(Order order) {
        try {
            return generateHtmlPdf(order);
        } catch (Exception htmlException) {
            Long orderId = order != null ? order.getOrderId() : null;
            log.warn("HTML invoice generation failed for order {}. Falling back to direct PDF rendering.", orderId, htmlException);

            try {
                return generateDirectPdf(order);
            } catch (Exception fallbackException) {
                htmlException.addSuppressed(fallbackException);
                throw new RuntimeException("Failed to generate invoice PDF", htmlException);
            }
        }
    }

    private byte[] generateHtmlPdf(Order order) {
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
        LocalDateTime invoiceDate = resolveInvoiceDate(order);
        User buyer = order.getBuyer();
        User seller = resolveSeller(order);

        context.setVariable("logoImageSrc", resolveImageSrc("static/images/invoice-logo.png"));
        context.setVariable("tickImageSrc", resolveImageSrc("static/images/invoice-tick.png"));
        context.setVariable("amount", formatCurrency(order.getAmount()));
        context.setVariable(
                "buyerName",
                buyer != null ? defaultText(buyer.getFullName(), "N/A") : "N/A"
        );
        context.setVariable(
                "sellerName",
                seller != null ? defaultText(seller.getFullName(), "N/A") : "N/A"
        );
        context.setVariable("invoiceDate", invoiceDate != null ? invoiceDate.format(INVOICE_DATE_FORMAT) : "N/A");
        context.setVariable("orderReference", formatOrderReference(order.getOrderId()));
        context.setVariable("paymentMode", PAYMENT_MODE);
        context.setVariable("statusLabel", resolveStatusLabel(order));
        context.setVariable(
                "cropName",
                listing != null && listing.getCrop() != null ? defaultText(listing.getCrop().getCropName(), "N/A") : "N/A"
        );
        context.setVariable("variety", listing != null ? defaultText(listing.getVariety(), "N/A") : "N/A");
    }

    private byte[] generateDirectPdf(Order order) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Document document = new Document(PageSize.A4, 36f, 36f, 36f, 36f);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18f, new Color(46, 125, 50));
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12f, Color.DARK_GRAY);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11f, new Color(84, 84, 84));
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 11f, Color.BLACK);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 10f, Color.GRAY);

            Paragraph title = new Paragraph("KisanSetu Invoice", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Transaction Successful", subtitleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(18f);
            document.add(subtitle);

            PdfPTable detailsTable = new PdfPTable(2);
            detailsTable.setWidthPercentage(100f);
            detailsTable.setWidths(new float[]{1.5f, 2.5f});
            detailsTable.setSpacingAfter(18f);

            addDetailsRow(detailsTable, "Amount", formatAmountLabel(order), labelFont, valueFont);
            addDetailsRow(detailsTable, "Buyer", resolveBuyerName(order), labelFont, valueFont);
            addDetailsRow(detailsTable, "Crop", resolveCropName(order), labelFont, valueFont);
            addDetailsRow(detailsTable, "Seller", resolveSellerName(order), labelFont, valueFont);
            addDetailsRow(detailsTable, "Variety", resolveVariety(order), labelFont, valueFont);
            addDetailsRow(detailsTable, "Invoice Date", formatInvoiceDate(resolveInvoiceDate(order)), labelFont, valueFont);
            addDetailsRow(detailsTable, "Order ID", formatOrderReference(order.getOrderId()), labelFont, valueFont);
            addDetailsRow(detailsTable, "Payment Mode", PAYMENT_MODE, labelFont, valueFont);
            addDetailsRow(detailsTable, "Status", resolveStatusLabel(order), labelFont, valueFont);

            document.add(detailsTable);

            Paragraph footer = new Paragraph("Thank you for your business!", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException exception) {
            throw new RuntimeException("Failed to generate fallback invoice PDF", exception);
        }
    }

    private void addDetailsRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setPadding(8f);
        labelCell.setBorderColor(new Color(220, 220, 220));
        labelCell.setBackgroundColor(new Color(248, 248, 248));

        PdfPCell valueCell = new PdfPCell(new Phrase(defaultText(value, "N/A"), valueFont));
        valueCell.setPadding(8f);
        valueCell.setBorderColor(new Color(220, 220, 220));

        table.addCell(labelCell);
        table.addCell(valueCell);
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

    private String formatOrderReference(Long orderId) {
        long safeOrderId = orderId != null ? orderId : 0L;
        return String.valueOf(safeOrderId);
    }

    private String defaultText(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }

    private LocalDateTime resolveInvoiceDate(Order order) {
        return order.getCompletedAt() != null ? order.getCompletedAt() : order.getCreatedAt();
    }

    private User resolveSeller(Order order) {
        if (order.getSeller() != null) {
            return order.getSeller();
        }

        Listing listing = order.getListing();
        return listing != null ? listing.getSeller() : null;
    }

    private String resolveBuyerName(Order order) {
        User buyer = order.getBuyer();
        return buyer != null ? defaultText(buyer.getFullName(), "N/A") : "N/A";
    }

    private String resolveSellerName(Order order) {
        User seller = resolveSeller(order);
        return seller != null ? defaultText(seller.getFullName(), "N/A") : "N/A";
    }

    private String resolveCropName(Order order) {
        Listing listing = order.getListing();
        return listing != null && listing.getCrop() != null
                ? defaultText(listing.getCrop().getCropName(), "N/A")
                : "N/A";
    }

    private String resolveVariety(Order order) {
        Listing listing = order.getListing();
        return listing != null ? defaultText(listing.getVariety(), "N/A") : "N/A";
    }

    private String formatInvoiceDate(LocalDateTime invoiceDate) {
        return invoiceDate != null ? invoiceDate.format(INVOICE_DATE_FORMAT) : "N/A";
    }

    private String formatAmountLabel(Order order) {
        return "INR " + formatCurrency(order.getAmount());
    }

    private String resolveImageSrc(String classpathLocation) {
        try {
            return new ClassPathResource(classpathLocation).getURL().toExternalForm();
        } catch (Exception exception) {
            log.warn("Failed to resolve invoice image {}", classpathLocation, exception);
            return "";
        }
    }

    private String resolveStatusLabel(Order order) {
        if (order == null) {
            return "VERIFIED";
        }

        if (order.getStatus() != null) {
            return switch (order.getStatus()) {
                case COMPLETED -> "VERIFIED";
                default -> formatEnumLabel(order.getStatus().name());
            };
        }

        if (order.getEscrowStatus() != null) {
            return switch (order.getEscrowStatus()) {
                case RELEASED -> "VERIFIED";
                default -> formatEnumLabel(order.getEscrowStatus().name());
            };
        }

        return "VERIFIED";
    }

    private String formatEnumLabel(String value) {
        return value.replace('_', ' ');
    }
}
