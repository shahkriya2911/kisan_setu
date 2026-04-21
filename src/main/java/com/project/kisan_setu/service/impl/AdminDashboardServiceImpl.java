package com.project.kisan_setu.service.impl;
import com.project.kisan_setu.dto.ResponseDto.*;
import com.project.kisan_setu.entity.Dispute;
import com.project.kisan_setu.entity.Report;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.enums.DisputeStatus;
import com.project.kisan_setu.enums.OrderStatus;
import com.project.kisan_setu.enums.ReportStatus;
import com.project.kisan_setu.repository.*;
import com.project.kisan_setu.service.AdminDashboardService;
import com.project.kisan_setu.service.EmailService;
import com.project.kisan_setu.util.ValidatorMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final ValidatorMethods validatorMethods;
    private final OrderRepository orderRepository;
    private final EmailService emailService;
    private final ReportUserRepository reportUserRepository;
    private final JavaMailSender javaMailSender;
    private final DisputeRepository disputeRepository;

    private static final int MAX_VIOLATIONS = 5;
    @Override
    public AdminDashboardResponseDto getDashboardOverview() {

        long totalSeller = listingRepository.countDistinctSellers();
        long activeListings = listingRepository.countByStatus(AuctionStatus.ACTIVE);
        long activeAuctions = listingRepository.countByStatus(AuctionStatus.ACTIVE);

        return new AdminDashboardResponseDto(
                totalSeller,
                activeListings,
                activeAuctions
        );
    }

    @Override
    public List<Object[]> getTopCommodities() {
        return listingRepository.getTopCommodities();
    }

    @Override
    public UserDistributionDto getUserDistribution() {

        validatorMethods.validateAdminAccess();

        Long sellers = listingRepository.countDistinctSellers();

        return new UserDistributionDto(sellers);
    }

    @Override
    public List<UserManagementDto> getAllUsersForAdmin(String type, String status) {

        validatorMethods.validateAdminAccess();

        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> {

                    long listings = listingRepository.countBySellerUserId(user.getUserId());
                    long orders = orderRepository.countByBuyerUserId(user.getUserId());
                    boolean isVerified = validatorMethods.isUserFullyVerified(user.getUserId());

                    boolean matchesType = true;
                    boolean matchesStatus = true;

                    // TYPE FILTER
                    if (type != null && !type.isBlank()) {
                        if (type.equalsIgnoreCase("SELLER")) {
                            matchesType = listings > 0;
                        } else if (type.equalsIgnoreCase("BUYER")) {
                            matchesType = orders > 0;
                        }
                    }

                    // STATUS FILTER
                    if (status != null && !status.isBlank()) {
                        if (status.equalsIgnoreCase("VERIFIED")) {
                            matchesStatus = isVerified;
                        } else if (status.equalsIgnoreCase("UNVERIFIED")) {
                            matchesStatus = !isVerified;
                        }
                    }

                    if (!(matchesType && matchesStatus)) {
                        return null;
                    }

                    String userType;
                    if (listings > 0 && orders > 0) userType = "Both";
                    else if (listings > 0) userType = "Seller";
                    else if (orders > 0) userType = "Buyer";
                    else userType = "New";

                    return new UserManagementDto(
                            user.getUserId(),
                            user.getFullName(),
                            userType,
                            user.getFarmLocation(),
                            isVerified ? "Verified" : "Unverified",
                            listings,
                            orders,
                            user.getStatus()
                    );
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public String verifyUser(Long userId) {

        validatorMethods.validateAdminAccess();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        long soldListings = listingRepository.findBySellerUserId(userId)
                .stream()
                .filter(listing ->
                        orderRepository.existsByListing_ListingIdAndStatus(
                                listing.getListingId(),
                                OrderStatus.COMPLETED
                        )
                ).count();

        long successfulOrders =
                orderRepository.countByBuyerUserIdAndStatus(
                        userId,
                        OrderStatus.COMPLETED
                );

        boolean sellerVerified = soldListings >= 5;
        boolean buyerVerified = successfulOrders >= 5;

        user.setSellerVerified(sellerVerified);
        user.setBuyerVerified(buyerVerified);

        userRepository.save(user);

        return "User verified. SellerVerified=" + sellerVerified +
                ", BuyerVerified=" + buyerVerified;
    }

    @Override
    public String suspendUser(Long userId) {

        validatorMethods.validateAdminAccess();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (Boolean.TRUE.equals(user.getSuspended())) {
            return "User already suspended";
        }

        if (user.getViolationCount() >= MAX_VIOLATIONS) {

            user.setSuspended(true);
            userRepository.save(user);

            return "User suspended due to " + MAX_VIOLATIONS + " or more violations.";

        } else {
            return "User cannot be suspended. Current violations: "
                    + user.getViolationCount();
        }
    }
    @Override
    public String reactivateUser(Long userId) {

        validatorMethods.validateAdminAccess();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!Boolean.TRUE.equals(user.getSuspended())) {
            return "User is already active";
        }

        user.setSuspended(false);
        user.setViolationCount(0);

        userRepository.save(user);

        return "User account has been reactivated.";
    }
    @Override
    public List<User> getFlaggedUsers() {

        validatorMethods.validateAdminAccess();

        return userRepository.findAll()
                .stream()
                .filter(user -> user.getViolationCount() >= MAX_VIOLATIONS)
                .toList();
    }

    @Override
    public List<ReportResponseDto> getAllReports() {
        List<Report> reports = reportUserRepository.findAll();

        return reports.stream().map(report -> {
            ReportResponseDto dto = new ReportResponseDto();

            dto.setReportId(report.getReportId());
            dto.setBuyerName(String.valueOf(report.getBuyer()));
            dto.setSellerName(String.valueOf(report.getSeller()));
            dto.setIssueType(report.getReason());
            dto.setStatus(report.getReportStatus().name());
            dto.setCreatedAt(report.getCreatedAt());

            return dto;
        }).toList();
    }

    @Override
    public ReportResponseDto updateStatus(Long reportId, String status) {
        Report report = reportUserRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        ReportStatus newStatus = ReportStatus.valueOf(status);

        ReportResponseDto responseDto = new ReportResponseDto();
        responseDto.setReportId(report.getReportId());
        responseDto.setBuyerName(report.getBuyer().getFullName());
        responseDto.setSellerName(report.getSeller().getFullName());
        responseDto.setStatus(report.getReportStatus().name());
        responseDto.setCreatedAt(report.getCreatedAt());


        return responseDto;
    }

    @Override
    public String requestMoreEvidence(Long disputeId) {

        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new RuntimeException("Dispute not found"));
        dispute.setStatus(DisputeStatus.UNDER_REVIEW);
        dispute.setEvidenceRequested(true);

        disputeRepository.save(dispute);

        emailService.sendDisputeEvidenceEmail(
                dispute.getBuyer().getEmail(),
                dispute.getBuyer().getFullName(),
                dispute.getDisputeCode()
        );

        emailService.sendDisputeResolvedEmail(
                dispute.getSeller().getEmail(),
                dispute.getSeller().getFullName(),
                dispute.getDisputeCode()
        );

        return "Evidence request sent successfully";
    }
    @Override
    public String resolveDispute(Long disputeId) {

        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new RuntimeException("Dispute not found"));

        dispute.setStatus(DisputeStatus.RESOLVED);

        disputeRepository.save(dispute);

        emailService.sendDisputeResolvedEmail(
                dispute.getBuyer().getEmail(),
                dispute.getBuyer().getFullName(),
                dispute.getDisputeCode()
        );

        emailService.sendDisputeResolvedEmail(
                dispute.getSeller().getEmail(),
                dispute.getSeller().getFullName(),
                dispute.getDisputeCode()
        );

        return "Dispute resolved and email sent successfully";
    }

}