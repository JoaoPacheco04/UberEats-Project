package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.dto.request.CreateCourseRequest;
import com.eduscrum.upt.Ubereats.dto.request.UpdateCourseRequest;
import com.eduscrum.upt.Ubereats.dto.response.CourseResponse;
import com.eduscrum.upt.Ubereats.entity.Course;
import com.eduscrum.upt.Ubereats.entity.CourseEnrollment;
import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.entity.enums.UserRole;
import com.eduscrum.upt.Ubereats.entity.enums.Semester;
import com.eduscrum.upt.Ubereats.repository.CourseRepository;
import com.eduscrum.upt.Ubereats.repository.UserRepository;
import com.eduscrum.upt.Ubereats.repository.CourseEnrollmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for CourseService.
 * Tests course CRUD operations with database.
 *
 * @author UberEats
 * @version 0.5.0
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseEnrollmentRepository enrollmentRepository;

    private User teacher;
    private User student;

    @BeforeEach
    void setUp() {
        // Clear data to ensure clean state
        enrollmentRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();

        // Create a teacher
        teacher = new User();
        teacher.setFirstName("Prof. John");
        teacher.setLastName("Doe");
        teacher.setEmail("teacher@test.com");
        teacher.setUsername("teachertest");
        teacher.setPassword("password");
        teacher.setRole(UserRole.TEACHER);
        userRepository.save(teacher);

        // Create a student
        student = new User();
        student.setFirstName("Jane");
        student.setLastName("Student");
        student.setEmail("student@test.com");
        student.setUsername("studenttest");
        student.setPassword("password");
        student.setRole(UserRole.STUDENT);
        userRepository.save(student);
    }

    @Test
    void createCourse_Success() {
        CreateCourseRequest request = new CreateCourseRequest();
        request.setName("Software Engineering");
        request.setCode("SE101");
        request.setDescription("Intro to SE");
        request.setSemester(Semester.FIRST);
        request.setAcademicYear("2024-2025");

        CourseResponse response = courseService.createCourse(request, teacher.getEmail());

        assertNotNull(response.getId());
        assertEquals("SE101", response.getCode());
        assertEquals(teacher.getEmail(), response.getTeacherEmail());
    }

    @Test
    void createCourse_DuplicateCode_ThrowsException() {
        // Create first course
        CreateCourseRequest request1 = new CreateCourseRequest();
        request1.setName("Course 1");
        request1.setCode("CS101");
        request1.setSemester(Semester.FIRST);
        request1.setAcademicYear("2024-2025");
        courseService.createCourse(request1, teacher.getEmail());

        // Try to create second course with same code
        CreateCourseRequest request2 = new CreateCourseRequest();
        request2.setName("Course 2");
        request2.setCode("CS101"); // Duplicate
        request2.setSemester(Semester.FIRST);
        request2.setAcademicYear("2024-2025");

        assertThrows(ResponseStatusException.class, () -> {
            courseService.createCourse(request2, teacher.getEmail());
        });
    }

    @Test
    void getCoursesByTeacher_ReturnsCorrectCourses() {
        // Create two courses for the teacher
        CreateCourseRequest request1 = new CreateCourseRequest();
        request1.setName("Course A");
        request1.setCode("A101");
        request1.setSemester(Semester.FIRST);
        request1.setAcademicYear("2024-2025");
        courseService.createCourse(request1, teacher.getEmail());

        CreateCourseRequest request2 = new CreateCourseRequest();
        request2.setName("Course B");
        request2.setCode("B101");
        request2.setSemester(Semester.FIRST);
        request2.setAcademicYear("2024-2025");
        courseService.createCourse(request2, teacher.getEmail());

        List<CourseResponse> courses = courseService.getCoursesByTeacher(teacher.getEmail());

        assertEquals(2, courses.size());
    }

    @Test
    void updateCourse_Success() {
        // Setup existing course
        CreateCourseRequest createRequest = new CreateCourseRequest();
        createRequest.setName("Old Name");
        createRequest.setCode("UPD101");
        createRequest.setSemester(Semester.FIRST);
        createRequest.setAcademicYear("2024-2025");
        CourseResponse created = courseService.createCourse(createRequest, teacher.getEmail());

        // Update
        UpdateCourseRequest updateRequest = new UpdateCourseRequest();
        updateRequest.setName("New Name");

        CourseResponse updated = courseService.updateCourse(created.getId(), updateRequest);

        assertEquals("New Name", updated.getName());
        assertEquals("UPD101", updated.getCode()); // Should remain unchanged
    }

    @Test
    void deleteCourse_SoftDelete_Success() {
        CreateCourseRequest createRequest = new CreateCourseRequest();
        createRequest.setName("To Delete");
        createRequest.setCode("DEL101");
        createRequest.setSemester(Semester.FIRST);
        createRequest.setAcademicYear("2024-2025");
        CourseResponse created = courseService.createCourse(createRequest, teacher.getEmail());

        Long courseId = created.getId();
        courseService.deleteCourse(courseId);

        // Verify it's no longer in active courses
        List<CourseResponse> activeCourses = courseService.getActiveCourses();
        boolean exists = activeCourses.stream().anyMatch(c -> c.getId().equals(courseId));
        assertFalse(exists, "Deleted course should not be in active courses list");

        // Verify we can still get it by ID but it is inactive
        CourseResponse retrieved = courseService.getCourseById(courseId);
        assertFalse(retrieved.getActive());
    }

    @Test
    void getCourseById_Success() {
        CreateCourseRequest request = new CreateCourseRequest();
        request.setName("Get By ID");
        request.setCode("ID101");
        request.setSemester(Semester.SECOND);
        request.setAcademicYear("2024-2025");
        CourseResponse created = courseService.createCourse(request, teacher.getEmail());

        CourseResponse found = courseService.getCourseById(created.getId());

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("Get By ID", found.getName());
    }

    @Test
    void getCourseById_NotFound_ThrowsException() {
        assertThrows(ResponseStatusException.class, () -> {
            courseService.getCourseById(999L);
        });
    }

    @Test
    void isCourseTeacher_ReturnsCorrectResult() {
        CreateCourseRequest request = new CreateCourseRequest();
        request.setName("Teacher Check");
        request.setCode("TCH101");
        request.setSemester(Semester.FIRST);
        request.setAcademicYear("2024-2025");
        CourseResponse created = courseService.createCourse(request, teacher.getEmail());

        // Check correct teacher
        assertTrue(courseService.isCourseTeacher(created.getId(), teacher.getEmail()));

        // Check incorrect teacher (student email)
        assertFalse(courseService.isCourseTeacher(created.getId(), student.getEmail()));

        // Check non-existent user
        assertFalse(courseService.isCourseTeacher(created.getId(), "nonexistent@test.com"));
    }

    @Test
    void searchCourses_ReturnsMatchingCourses() {
        CreateCourseRequest req1 = new CreateCourseRequest();
        req1.setName("Math Advanced");
        req1.setCode("MTH201");
        req1.setSemester(Semester.FIRST);
        req1.setAcademicYear("2024-2025");
        courseService.createCourse(req1, teacher.getEmail());

        CreateCourseRequest req2 = new CreateCourseRequest();
        req2.setName("Physics Basic");
        req2.setCode("PHY101");
        req2.setSemester(Semester.FIRST);
        req2.setAcademicYear("2024-2025");
        courseService.createCourse(req2, teacher.getEmail());

        List<CourseResponse> mathResults = courseService.searchCourses("Math");
        assertEquals(1, mathResults.size());
        assertEquals("MTH201", mathResults.get(0).getCode());

        List<CourseResponse> phyResults = courseService.searchCourses("PHY");
        assertEquals(1, phyResults.size());
        assertEquals("Physics Basic", phyResults.get(0).getName());

        List<CourseResponse> emptyResults = courseService.searchCourses("Biology");
        assertEquals(0, emptyResults.size());
    }

    @Test
    void getCoursesByAcademicPeriod_ReturnsCorrectCourses() {
        // Course 1: 2024-2025, Semester 1
        CreateCourseRequest req1 = new CreateCourseRequest();
        req1.setName("Course 1");
        req1.setCode("C1");
        req1.setSemester(Semester.FIRST);
        req1.setAcademicYear("2024-2025");
        courseService.createCourse(req1, teacher.getEmail());

        // Course 2: 2024-2025, Semester 2
        CreateCourseRequest req2 = new CreateCourseRequest();
        req2.setName("Course 2");
        req2.setCode("C2");
        req2.setSemester(Semester.SECOND);
        req2.setAcademicYear("2024-2025");
        courseService.createCourse(req2, teacher.getEmail());

        // Course 3: 2025-2026, Semester 1
        CreateCourseRequest req3 = new CreateCourseRequest();
        req3.setName("Course 3");
        req3.setCode("C3");
        req3.setSemester(Semester.FIRST);
        req3.setAcademicYear("2025-2026");
        courseService.createCourse(req3, teacher.getEmail());

        List<CourseResponse> results = courseService.getCoursesByAcademicPeriod("2024-2025", Semester.FIRST);

        assertEquals(1, results.size());
        assertEquals("C1", results.get(0).getCode());
    }

    @Test
    void isStudentEnrolled_ReturnsCorrectResult() {
        CreateCourseRequest request = new CreateCourseRequest();
        request.setName("Enrollment Check");
        request.setCode("ENR101");
        request.setSemester(Semester.FIRST);
        request.setAcademicYear("2024-2025");
        CourseResponse created = courseService.createCourse(request, teacher.getEmail());

        // Enroll student manually using repository
        Course course = courseRepository.findById(created.getId()).orElseThrow();
        CourseEnrollment enrollment = new CourseEnrollment(course, student);
        enrollmentRepository.save(enrollment);

        // Check enrollment
        assertTrue(courseService.isStudentEnrolled(created.getId(), student.getEmail()));

        // Check not enrolled
        assertFalse(courseService.isStudentEnrolled(created.getId(), teacher.getEmail()));
    }
}
