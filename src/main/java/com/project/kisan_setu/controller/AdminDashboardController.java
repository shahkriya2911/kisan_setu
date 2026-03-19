package com.project.kisan_setu.controller;
import com.project.kisan_setu.dto.ResponseDto.AdminDashboardResponseDto;
import com.project.kisan_setu.dto.ResponseDto.TopCommodityDto;
import com.project.kisan_setu.dto.ResponseDto.UserDistributionDto;
import com.project.kisan_setu.dto.ResponseDto.UserManagementDto;
import com.project.kisan_setu.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
public class AdminDashboardController {
    private final AdminDashboardService adminDashboardService;

    @GetMapping("/overview")
    public ResponseEntity<AdminDashboardResponseDto> getDashboardOverview() {
        AdminDashboardResponseDto response = adminDashboardService.getDashboardOverview();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-commodities")
    public List<Object[]> getTopCommodities() {
        return adminDashboardService.getTopCommodities();
    }

    @GetMapping("/user-distribution")
    public UserDistributionDto getUserDistribution() {
        return adminDashboardService.getUserDistribution();
    }

    @GetMapping("/admin/users")
    public List<UserManagementDto> getAllUsersForAdmin(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        System.out.println("Controller status = " + status);
        return adminDashboardService.getAllUsersForAdmin(type, status);
    }

    @PutMapping("/verify/{userId}")
    public ResponseEntity<String> verifyUser(@PathVariable Long userId) {
        String result = adminDashboardService.verifyUser(userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/suspend/{userId}")
    public ResponseEntity<String> suspendUser(@PathVariable Long userId) {
        String result = adminDashboardService.suspendUser(userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/reactivate/{userId}")
    public ResponseEntity<String> reactivateUser(@PathVariable Long userId) {
        String result = adminDashboardService.reactivateUser(userId);
        return ResponseEntity.ok(result);
    }

}