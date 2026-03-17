package com.project.kisan_setu.controller;
import com.project.kisan_setu.dto.ResponseDto.AdminDashboardResponseDto;
import com.project.kisan_setu.dto.ResponseDto.TopCommodityDto;
import com.project.kisan_setu.dto.ResponseDto.UserDistributionDto;
import com.project.kisan_setu.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {
    private final AdminDashboardService dashboardService;

    @GetMapping("/overview")
    public ResponseEntity<AdminDashboardResponseDto> getDashboardOverview() {
        AdminDashboardResponseDto response = dashboardService.getDashboardOverview();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-commodities")
    public List<Object[]> getTopCommodities() {
        return dashboardService.getTopCommodities();
    }

    @GetMapping("/user-distribution")
    public UserDistributionDto getUserDistribution() {
        return dashboardService.getUserDistribution();
    }

}