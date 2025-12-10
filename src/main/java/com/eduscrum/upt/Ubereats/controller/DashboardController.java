package com.eduscrum.upt.Ubereats.controller;

import com.eduscrum.upt.Ubereats.dto.response.DashboardStatsDTO;
import com.eduscrum.upt.Ubereats.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for dashboard statistics in the EduScrum platform.
 * Provides endpoints for retrieving student dashboard data.
 *
 * @version 0.1.0 (2025-10-15)
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Constructs a new DashboardController with required dependencies.
     *
     * @param dashboardService Service for dashboard operations
     */
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Gets dashboard statistics for a student.
     *
     * @param studentId The ID of the student
     * @return ResponseEntity containing the dashboard statistics
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<DashboardStatsDTO> getStudentDashboard(@PathVariable Long studentId) {
        DashboardStatsDTO stats = dashboardService.getStudentDashboardStats(studentId);
        return ResponseEntity.ok(stats);
    }
}
