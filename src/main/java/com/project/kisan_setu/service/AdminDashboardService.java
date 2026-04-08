package com.project.kisan_setu.service;
import com.project.kisan_setu.dto.ResponseDto.*;
import com.project.kisan_setu.entity.User;
import java.util.List;

public interface AdminDashboardService {

       AdminDashboardResponseDto getDashboardOverview();
//     List<DailyTransactionDto> getDailyTransactions();
       List<Object[]> getTopCommodities();
       UserDistributionDto getUserDistribution();
//     List<EscrowGrowthDto> getEscrowGrowth();
       List<UserManagementDto> getAllUsersForAdmin(String type, String status);
       String verifyUser(Long userId);
       String suspendUser(Long userId);
       String reactivateUser(Long userId);
       List<User> getFlaggedUsers();
       List<ReportResponseDto> getAllReports();
       ReportResponseDto updateStatus(Long reportId, String status);
       String requestMoreEvidence(Long disputeId);

    String resolveDispute(Long disputeId);
}
