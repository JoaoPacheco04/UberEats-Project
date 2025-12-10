package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.dto.request.CreateCourseRequest;
import com.eduscrum.upt.Ubereats.dto.request.UpdateCourseRequest;
import com.eduscrum.upt.Ubereats.dto.response.CourseResponse;
import com.eduscrum.upt.Ubereats.entity.Course;
import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.entity.enums.Semester;
import com.eduscrum.upt.Ubereats.repository.CourseRepository;
import com.eduscrum.upt.Ubereats.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing courses in the EduScrum platform.
 * Handles course creation, updates, retrieval, and enrollment verification.
 *
 * @version 0.9.1 (2025-11-28)
 */
@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseEnrollmentService enrollmentService;

    /**
     * Constructs a new CourseService with required dependencies.
     *
     * @param courseRepository  Repository for course data access
     * @param userRepository    Repository for user data access
     * @param enrollmentService Service for enrollment operations
     */
    public CourseService(CourseRepository courseRepository, UserRepository userRepository,
            CourseEnrollmentService enrollmentService) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentService = enrollmentService;
    }

    /**
     * Creates a new course with the specified details.
     *
     * @param request      The request containing course details
     * @param teacherEmail The email of the teacher creating the course
     * @return The created course as a response DTO
     * @throws ResponseStatusException if course code exists or teacher not found
     */
    public CourseResponse createCourse(CreateCourseRequest request, String teacherEmail) {
        // Validate course code uniqueness
        if (courseRepository.existsByCode(request.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Course code already exists: " + request.getCode());
        }

        // Find teacher
        User teacher = userRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Teacher not found: " + teacherEmail));

        // Validate teacher role
        if (!teacher.isTeacher()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User is not a teacher: " + teacherEmail);
        }

        // Create and save course
        Course course = new Course(
                request.getName(),
                request.getCode(),
                request.getDescription(),
                request.getSemester(),
                request.getAcademicYear(),
                teacher);

        Course savedCourse = courseRepository.save(course);
        return convertToResponse(savedCourse);
    }

    /**
     * Gets a course by its ID.
     *
     * @param courseId The ID of the course to retrieve
     * @return The course as a response DTO
     * @throws ResponseStatusException if course not found
     */
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Course not found with id: " + courseId));
        return convertToResponse(course);
    }

    /**
     * Gets all courses for a specific teacher.
     *
     * @param teacherEmail The email of the teacher
     * @return List of courses taught by the teacher
     * @throws ResponseStatusException if teacher not found
     */
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesByTeacher(String teacherEmail) {
        User teacher = userRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Teacher not found: " + teacherEmail));

        return courseRepository.findByTeacher(teacher).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Gets all active courses in the system.
     *
     * @return List of active courses
     */
    @Transactional(readOnly = true)
    public List<CourseResponse> getActiveCourses() {
        return courseRepository.findByIsActiveTrue().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing course with the provided details.
     *
     * @param courseId The ID of the course to update
     * @param request  The request containing updated course details
     * @return The updated course as a response DTO
     * @throws ResponseStatusException if course not found
     */
    public CourseResponse updateCourse(Long courseId, UpdateCourseRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Course not found with id: " + courseId));

        // Update fields if provided
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            course.setName(request.getName());
        }
        if (request.getDescription() != null) {
            course.setDescription(request.getDescription());
        }
        if (request.getSemester() != null) {
            course.setSemester(request.getSemester());
        }
        if (request.getAcademicYear() != null && !request.getAcademicYear().trim().isEmpty()) {
            course.setAcademicYear(request.getAcademicYear());
        }
        if (request.getActive() != null) {
            course.setIsActive(request.getActive());
        }

        Course updatedCourse = courseRepository.save(course);
        return convertToResponse(updatedCourse);
    }

    /**
     * Deletes (deactivates) a course by setting it as inactive.
     *
     * @param courseId The ID of the course to delete
     * @throws ResponseStatusException if course not found
     */
    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Course not found with id: " + courseId));

        // Soft delete - deactivate
        course.setIsActive(false);
        courseRepository.save(course);
    }

    /**
     * Checks if a user is the teacher of a specific course.
     *
     * @param courseId     The ID of the course
     * @param teacherEmail The email of the teacher to check
     * @return true if the user is the course teacher, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isCourseTeacher(Long courseId, String teacherEmail) {
        return courseRepository.isCourseTeacher(courseId, teacherEmail);
    }

    /**
     * Checks if a student is enrolled in a specific course.
     * This is used for @PreAuthorize security checks in the CourseController.
     *
     * @param courseId     The ID of the course
     * @param studentEmail The email of the student to check
     * @return true if the student is enrolled, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isStudentEnrolled(Long courseId, String studentEmail) {
        // 1. Find student by email
        Optional<User> studentOpt = userRepository.findByEmail(studentEmail);

        if (studentOpt.isEmpty()) {
            return false; // User does not exist, cannot be enrolled
        }

        // 2. Delegate the check to the CourseEnrollmentService
        return enrollmentService.isStudentEnrolled(courseId, studentOpt.get().getId());
    }

    /**
     * Gets courses by academic year and semester.
     *
     * @param academicYear The academic year to filter by
     * @param semester     The semester to filter by
     * @return List of courses matching the criteria
     */
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesByAcademicPeriod(String academicYear, Semester semester) {
        return courseRepository.findByAcademicYearAndSemesterAndIsActiveTrue(academicYear, semester)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Searches for courses by name or code.
     *
     * @param searchTerm The search term to match against name or code
     * @return List of courses matching the search term
     */
    @Transactional(readOnly = true)
    public List<CourseResponse> searchCourses(String searchTerm) {
        // This would need a custom repository method
        // For now, filter from all active courses
        return courseRepository.findByIsActiveTrue().stream()
                .filter(course -> course.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        course.getCode().toLowerCase().contains(searchTerm.toLowerCase()))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Converts a Course entity to a CourseResponse DTO.
     *
     * @param course The course entity to convert
     * @return The course as a response DTO
     */
    private CourseResponse convertToResponse(Course course) {
        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setName(course.getName());
        response.setCode(course.getCode());
        response.setDescription(course.getDescription());
        response.setSemester(course.getSemester());
        response.setAcademicYear(course.getAcademicYear());
        response.setActive(course.getIsActive());
        response.setCreatedAt(course.getCreatedAt());
        response.setUpdatedAt(course.getUpdatedAt());

        // Add teacher info
        if (course.getTeacher() != null) {
            response.setTeacherName(course.getTeacher().getFullName());
            response.setTeacherEmail(course.getTeacher().getEmail());
        }

        // Add statistics
        response.setStudentCount(course.getStudentCount());
        response.setProjectCount(course.getProjectCount());
        response.setAverageTeamScore(course.getAverageTeamScore());

        return response;
    }
}
