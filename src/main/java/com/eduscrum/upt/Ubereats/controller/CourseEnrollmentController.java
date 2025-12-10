package com.eduscrum.upt.Ubereats.controller;

import com.eduscrum.upt.Ubereats.dto.request.CourseEnrollmentRequestDTO;
import com.eduscrum.upt.Ubereats.dto.response.CourseEnrollmentResponseDTO;
import com.eduscrum.upt.Ubereats.service.CourseEnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin(origins = "*")
public class CourseEnrollmentController {

    private final CourseEnrollmentService enrollmentService;

    public CourseEnrollmentController(CourseEnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public ResponseEntity<CourseEnrollmentResponseDTO> enrollStudent(
            @Valid @RequestBody CourseEnrollmentRequestDTO request) {
        CourseEnrollmentResponseDTO enrollment = enrollmentService.enrollStudent(request.getCourseId(),
                request.getStudentId());
        return new ResponseEntity<>(enrollment, HttpStatus.CREATED);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<CourseEnrollmentResponseDTO>> getStudentEnrollments(@PathVariable Long studentId) {
        List<CourseEnrollmentResponseDTO> enrollments = enrollmentService.getStudentEnrollments(studentId);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<CourseEnrollmentResponseDTO>> getCourseEnrollments(@PathVariable Long courseId) {
        List<CourseEnrollmentResponseDTO> enrollments = enrollmentService.getCourseEnrollments(courseId);
        return ResponseEntity.ok(enrollments);
    }
}