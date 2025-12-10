package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.dto.response.CourseEnrollmentResponseDTO;
import com.eduscrum.upt.Ubereats.entity.Course;
import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.entity.enums.Semester;
import com.eduscrum.upt.Ubereats.entity.enums.UserRole;
import com.eduscrum.upt.Ubereats.repository.CourseEnrollmentRepository;
import com.eduscrum.upt.Ubereats.repository.CourseRepository;
import com.eduscrum.upt.Ubereats.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for CourseEnrollmentService.
 *
 * @version 1.1.1 (2025-12-09)
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
/**
 * Integration tests for CourseEnrollmentService.
 *
 * @version 1.1.1 (2025-12-09)
 */
class CourseEnrollmentServiceTest {

    @Autowired
    private CourseEnrollmentService courseEnrollmentService;

    @Autowired
    private CourseEnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    private Course course;
    private User teacher;
    private User student;

    @BeforeEach
    void setUp() {
        enrollmentRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();

        // Create Teacher
        teacher = new User();
        teacher.setFirstName("Prof");
        teacher.setLastName("Enroll");
        teacher.setEmail("prof@enroll.com");
        teacher.setUsername("profenroll");
        teacher.setPassword("password");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        // Create Student
        student = new User();
        student.setFirstName("Student");
        student.setLastName("One");
        student.setEmail("s1@enroll.com");
        student.setUsername("student1");
        student.setPassword("password");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        // Create Course
        course = new Course("Enrollment Course", "EC101", "Description", Semester.FIRST, "2024", teacher);
        course = courseRepository.save(course);
    }

    // ===================== ENROLL STUDENT TESTS =====================

    @Test
    void enrollStudent_Success() {
        CourseEnrollmentResponseDTO response = courseEnrollmentService.enrollStudent(course.getId(), student.getId());

        assertNotNull(response.getId());
        assertEquals(course.getId(), response.getCourseId());
        assertEquals(student.getId(), response.getStudentId());
        assertNotNull(response.getEnrolledAt());
    }

    @Test
    void enrollStudent_AlreadyEnrolled_ThrowsException() {
        courseEnrollmentService.enrollStudent(course.getId(), student.getId());

        assertThrows(IllegalStateException.class, () -> {
            courseEnrollmentService.enrollStudent(course.getId(), student.getId());
        });
    }

    @Test
    void enrollStudent_NotStudent_ThrowsException() {
        // Try to enroll the teacher
        assertThrows(IllegalArgumentException.class, () -> {
            courseEnrollmentService.enrollStudent(course.getId(), teacher.getId());
        });
    }

    @Test
    void enrollStudent_CourseNotFound_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            courseEnrollmentService.enrollStudent(999L, student.getId());
        });
    }

    @Test
    void enrollStudent_StudentNotFound_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            courseEnrollmentService.enrollStudent(course.getId(), 999L);
        });
    }

    // ===================== IS STUDENT ENROLLED TESTS =====================

    @Test
    void isStudentEnrolled_ReturnsTrue() {
        courseEnrollmentService.enrollStudent(course.getId(), student.getId());

        assertTrue(courseEnrollmentService.isStudentEnrolled(course.getId(), student.getId()));
    }

    @Test
    void isStudentEnrolled_ReturnsFalse_NotEnrolled() {
        assertFalse(courseEnrollmentService.isStudentEnrolled(course.getId(), student.getId()));
    }

    @Test
    void isStudentEnrolled_ReturnsFalse_NonExistentCourse() {
        assertFalse(courseEnrollmentService.isStudentEnrolled(999L, student.getId()));
    }

    // ===================== GET STUDENT ENROLLMENTS TESTS =====================

    @Test
    void getStudentEnrollments_ReturnsEnrollments() {
        // Create another course
        Course course2 = new Course("Course 2", "C2", "Desc", Semester.SECOND, "2024", teacher);
        course2 = courseRepository.save(course2);

        // Enroll student in both courses
        courseEnrollmentService.enrollStudent(course.getId(), student.getId());
        courseEnrollmentService.enrollStudent(course2.getId(), student.getId());

        List<CourseEnrollmentResponseDTO> enrollments = courseEnrollmentService.getStudentEnrollments(student.getId());

        assertEquals(2, enrollments.size());
    }

    @Test
    void getStudentEnrollments_NoEnrollments_ReturnsEmptyList() {
        List<CourseEnrollmentResponseDTO> enrollments = courseEnrollmentService.getStudentEnrollments(student.getId());

        assertTrue(enrollments.isEmpty());
    }

    @Test
    void getStudentEnrollments_NonExistentStudent_ReturnsEmptyList() {
        List<CourseEnrollmentResponseDTO> enrollments = courseEnrollmentService.getStudentEnrollments(999L);

        assertTrue(enrollments.isEmpty());
    }

    // ===================== MULTIPLE STUDENTS TESTS =====================

    @Test
    void enrollMultipleStudents_Success() {
        User student2 = new User();
        student2.setFirstName("Student");
        student2.setLastName("Two");
        student2.setEmail("s2@enroll.com");
        student2.setUsername("student2");
        student2.setPassword("password");
        student2.setRole(UserRole.STUDENT);
        student2 = userRepository.save(student2);

        courseEnrollmentService.enrollStudent(course.getId(), student.getId());
        courseEnrollmentService.enrollStudent(course.getId(), student2.getId());

        assertTrue(courseEnrollmentService.isStudentEnrolled(course.getId(), student.getId()));
        assertTrue(courseEnrollmentService.isStudentEnrolled(course.getId(), student2.getId()));
    }
}
