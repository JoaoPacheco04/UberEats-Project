package com.eduscrum.upt.Ubereats.controller;

import com.eduscrum.upt.Ubereats.service.ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for data export operations in the EduScrum platform.
 * Provides endpoints for exporting course data as CSV files.
 *
 * @version 0.2.1 (2025-10-22)
 */
@RestController
@RequestMapping("/api/export")
@CrossOrigin(origins = "*")
public class ExportController {

    private final ExportService exportService;

    /**
     * Constructs a new ExportController with required dependencies.
     *
     * @param exportService Service for export operations
     */
    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    /**
     * Exports course grades as a CSV file.
     *
     * @param courseId The ID of the course to export
     * @return ResponseEntity containing the CSV file as bytes
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<byte[]> exportCourseGrades(@PathVariable Long courseId) {
        byte[] csvData = exportService.generateCourseCsv(courseId);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"course_grades.csv\"");
        headers.setContentType(MediaType.parseMediaType("text/csv"));

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
    }
}
