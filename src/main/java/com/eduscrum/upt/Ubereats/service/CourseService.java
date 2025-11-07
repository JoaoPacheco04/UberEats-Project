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
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CourseService(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new course
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
                teacher
        );

        Course savedCourse = courseRepository.save(course);
        return convertToResponse(savedCourse);
    }

    /**
     * Get course by ID
     */
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Course not found with id: " + courseId));
        return convertToResponse(course);
    }


     //Get all courses for a teacher

    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesByTeacher(String teacherEmail) {
        User teacher = userRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Teacher not found: " + teacherEmail));

        return courseRepository.findByTeacher(teacher).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    //Get all active courses
    @Transactional(readOnly = true)
    public List<CourseResponse> getActiveCourses() {
        return courseRepository.findByIsActiveTrue().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }


     //Update course
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
     * Delete (deactivate) course
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
     * Check if user is the teacher of the course
     */
    @Transactional(readOnly = true)
    public boolean isCourseTeacher(Long courseId, String teacherEmail) {
        return courseRepository.isCourseTeacher(courseId, teacherEmail);
    }

    /**
     * Check if student is enrolled in course
     */
    @Transactional(readOnly = true)
    public boolean isStudentEnrolled(Long courseId, String studentEmail) {
        // Will implement when CourseEnrollment is created
        // For now, return false or implement basic check
        return false;
    }

    /**
     * Get courses by academic year and semester
     */
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesByAcademicPeriod(String academicYear, Semester semester) {
        return courseRepository.findByAcademicYearAndSemesterAndIsActiveTrue(academicYear, semester)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search courses by name or code
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
     * Convert Course entity to CourseResponse DTO
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