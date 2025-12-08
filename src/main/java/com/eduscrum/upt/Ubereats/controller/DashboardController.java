package com.eduscrum.upt.Ubereats.controller;

import com.eduscrum.upt.Ubereats.dto.response.DashboardStatsDTO;
import com.eduscrum.upt.Ubereats.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<DashboardStatsDTO> getStudentDashboard(@PathVariable Long studentId) {
        DashboardStatsDTO stats = dashboardService.getStudentDashboardStats(studentId);
        return ResponseEntity.ok(stats);
    }
}
