package com.eduscrum.upt.Ubereats.controller;

import com.eduscrum.upt.Ubereats.dto.request.CreateCourseRequest;
import com.eduscrum.upt.Ubereats.dto.request.UpdateCourseRequest;
import com.eduscrum.upt.Ubereats.dto.response.CourseResponse;
import com.eduscrum.upt.Ubereats.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing courses in the EduScrum platform.
 * Provides endpoints for course CRUD operations and teacher management.
 *
 * @version 1.1.0 (2025-12-08)
 */
@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseService courseService;

    /**
     * Constructs a new CourseController with required dependencies.
     *
     * @param courseService Service for course operations
     */
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * Creates a new course.
     *
     * @param request        The request containing course details
     * @param authentication The current user's authentication
     * @return ResponseEntity containing the created course
     */
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CourseResponse> createCourse(
            @Valid @RequestBody CreateCourseRequest request,
            Authentication authentication) {

        String teacherEmail = authentication.getName();
        CourseResponse course = courseService.createCourse(request, teacherEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(course);
    }

    /**
     * Gets all courses for the authenticated teacher.
     *
     * @param authentication The current user's authentication
     * @return ResponseEntity containing the list of courses
     */
    @GetMapping("/teacher/my-courses")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<CourseResponse>> getTeacherCourses(Authentication authentication) {
        String teacherEmail = authentication.getName();
        List<CourseResponse> courses = courseService.getCoursesByTeacher(teacherEmail);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public ResponseEntity<List<CourseResponse>> getAvailableCourses() {
        List<CourseResponse> courses = courseService.getActiveCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{courseId}")
    @PreAuthorize("hasRole('TEACHER') or @courseService.isStudentEnrolled(#courseId, authentication.name)")
    public ResponseEntity<CourseResponse> getCourse(@PathVariable Long courseId) {
        CourseResponse course = courseService.getCourseById(courseId);
        return ResponseEntity.ok(course);
    }

    @PutMapping("/{courseId}")
    @PreAuthorize("hasRole('TEACHER') and @courseService.isCourseTeacher(#courseId, authentication.name)")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody UpdateCourseRequest request) {

        CourseResponse course = courseService.updateCourse(courseId, request);
        return ResponseEntity.ok(course);
    }

    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('TEACHER') and @courseService.isCourseTeacher(#courseId, authentication.name)")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    // Additional endpoints
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public ResponseEntity<List<CourseResponse>> searchCourses(@RequestParam String q) {
        List<CourseResponse> courses = courseService.searchCourses(q);
        return ResponseEntity.ok(courses);
    }
}
