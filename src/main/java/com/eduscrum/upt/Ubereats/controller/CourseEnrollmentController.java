package com.eduscrum.upt.Ubereats.controller;

import com.eduscrum.upt.Ubereats.dto.request.CourseEnrollmentRequestDTO;
import com.eduscrum.upt.Ubereats.dto.response.CourseEnrollmentResponseDTO;
import com.eduscrum.upt.Ubereats.service.CourseEnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing course enrollments in the EduScrum platform.
 * Provides endpoints for student enrollment operations.
 *
 * @author UberEats
 * @version 1.2.0
 */
@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin(origins = "*")
public class CourseEnrollmentController {

    private final CourseEnrollmentService enrollmentService;

    /**
     * Constructs a new CourseEnrollmentController with required dependencies.
     *
     * @param enrollmentService Service for enrollment operations
     */
    public CourseEnrollmentController(CourseEnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    /**
     * Enrolls a student in a course.
     *
     * @param request The request containing enrollment details
     * @return ResponseEntity containing the created enrollment
     */
    @PostMapping
    public ResponseEntity<CourseEnrollmentResponseDTO> enrollStudent(
            @Valid @RequestBody CourseEnrollmentRequestDTO request) {
        CourseEnrollmentResponseDTO enrollment = enrollmentService.enrollStudent(request.getCourseId(),
                request.getStudentId());
        return new ResponseEntity<>(enrollment, HttpStatus.CREATED);
    }

    /**
     * Gets all enrollments for a student.
     *
     * @param studentId The ID of the student
     * @return ResponseEntity containing the list of enrollments
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<CourseEnrollmentResponseDTO>> getStudentEnrollments(@PathVariable Long studentId) {
        List<CourseEnrollmentResponseDTO> enrollments = enrollmentService.getStudentEnrollments(studentId);
        return ResponseEntity.ok(enrollments);
    }
}
