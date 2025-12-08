package com.eduscrum.upt.Ubereats.controller;

import com.eduscrum.upt.Ubereats.service.ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/export")
@CrossOrigin(origins = "*")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<byte[]> exportCourseGrades(@PathVariable Long courseId) {
        byte[] csvData = exportService.generateCourseCsv(courseId);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"course_grades.csv\"");
        headers.setContentType(MediaType.parseMediaType("text/csv")); // Or APPLICATION_OCTET_STREAM

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
    }
}
