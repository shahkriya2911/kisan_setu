package com.project.kisan_setu.controller;
import com.project.kisan_setu.dto.ResponseDto.*;
import com.project.kisan_setu.service.AdminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Dashboard Management", description = "Endpoints for admin dashboard related resources")
public class AdminDashboardController {
    private final AdminDashboardService adminDashboardService;
    private static final Logger logger = LoggerFactory.getLogger(AdminDashboardController.class);

    @GetMapping("/overview")
    @Operation(summary = "Get admin dashboard overview method", description = "Used by admin to get dashboard overview")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin dashboard overview fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<AdminDashboardResponseDto> getDashboardOverview() {
        AdminDashboardResponseDto response = adminDashboardService.getDashboardOverview();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-commodities")
    @Operation(summary = "Get top commodities method", description = "Used by admin to get top commodities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Top commodities fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public List<Object[]> getTopCommodities() {
        return adminDashboardService.getTopCommodities();
    }

    @GetMapping("/user-distribution")
    @Operation(summary = "Get user distribution method", description = "Used by admin to get user distribution data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User distribution fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public UserDistributionDto getUserDistribution() {
        return adminDashboardService.getUserDistribution();
    }

    @GetMapping("/admin/users")
    @Operation(summary = "Get all users for admin method", description = "Used by admin to get users with optional type and status filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public List<UserManagementDto> getAllUsersForAdmin(
            @Parameter(description = "User type filter", required = false)
            @RequestParam(required = false) String type,
            @Parameter(description = "User status filter", required = false)
            @RequestParam(required = false) String status) {
        logger.info("Controller status = " + status);
        return adminDashboardService.getAllUsersForAdmin(type, status);
    }

    @PutMapping("/verify/{userId}")
    @Operation(summary = "Verify user method", description = "Used by admin to verify a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User verified successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<String> verifyUser(
            @Parameter(description = "User ID request", required = true)
            @PathVariable Long userId) {
        String result = adminDashboardService.verifyUser(userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/suspend/{userId}")
    @Operation(summary = "Suspend user method", description = "Used by admin to suspend a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User suspended successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<String> suspendUser(
            @Parameter(description = "User ID request", required = true)
            @PathVariable Long userId) {
        String result = adminDashboardService.suspendUser(userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/reactivate/{userId}")
    @Operation(summary = "Reactivate user method", description = "Used by admin to reactivate a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User reactivated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<String> reactivateUser(
            @Parameter(description = "User ID request", required = true)
            @PathVariable Long userId) {
        String result = adminDashboardService.reactivateUser(userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/disputes")
    @Operation(summary = "Get all disputes method", description = "Used by admin to get all disputes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disputes fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<List<ReportResponseDto>> getAllDisputes(
            @Parameter(description = "Dispute status filter", required = false)
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(adminDashboardService.getAllReports());
    }

    @PutMapping("/{reportId}/status")
    @Operation(summary = "Update dispute status method", description = "Used by admin to update dispute report status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dispute status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Report not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<ReportResponseDto> updateStatus(
            @Parameter(description = "Report ID request", required = true)
            @PathVariable Long reportId,
            @Parameter(description = "Updated report status", required = true)
            @RequestParam String status
    ) {
        return ResponseEntity.ok(adminDashboardService.updateStatus(reportId, status));
    }

    @PostMapping("/request-evidence/{disputeId}")
    @Operation(summary = "Request dispute evidence method", description = "Used by admin to request more evidence for a dispute")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evidence requested successfully"),
            @ApiResponse(responseCode = "404", description = "Dispute not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<String> requestEvidence(
            @Parameter(description = "Dispute ID request", required = true)
            @PathVariable Long disputeId) {

        String response = adminDashboardService.requestMoreEvidence(disputeId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/resolve/{disputeId}")
    @Operation(summary = "Resolve dispute method", description = "Used by admin to resolve a dispute")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dispute resolved successfully"),
            @ApiResponse(responseCode = "404", description = "Dispute not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized user"),
            @ApiResponse(responseCode = "500", description = "Something went wrong")
    })
    @SecurityRequirement(name = "cookieAuth")
    public ResponseEntity<String> resolveDispute(
            @Parameter(description = "Dispute ID request", required = true)
            @PathVariable Long disputeId) {

        String response = adminDashboardService.resolveDispute(disputeId);

        return ResponseEntity.ok(response);
    }
}

