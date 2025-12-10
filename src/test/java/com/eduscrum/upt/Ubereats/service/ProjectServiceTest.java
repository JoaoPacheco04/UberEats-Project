package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.dto.request.CreateProjectRequest;
import com.eduscrum.upt.Ubereats.dto.request.UpdateProjectRequest;
import com.eduscrum.upt.Ubereats.dto.response.ProjectResponse;
import com.eduscrum.upt.Ubereats.entity.Course;
import com.eduscrum.upt.Ubereats.entity.Project;
import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.entity.enums.ProjectStatus;
import com.eduscrum.upt.Ubereats.entity.enums.UserRole;
import com.eduscrum.upt.Ubereats.entity.enums.Semester;
import com.eduscrum.upt.Ubereats.repository.CourseRepository;
import com.eduscrum.upt.Ubereats.repository.ProjectRepository;
import com.eduscrum.upt.Ubereats.repository.UserRepository;
import com.eduscrum.upt.Ubereats.exception.ResourceNotFoundException;
import com.eduscrum.upt.Ubereats.exception.BusinessLogicException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    private Course course;
    private User teacher;

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();

        // Setup Teacher
        teacher = new User();
        teacher.setFirstName("Prof");
        teacher.setLastName("X");
        teacher.setEmail("profx@test.com");
        teacher.setUsername("profx");
        teacher.setPassword("pass");
        teacher.setRole(UserRole.TEACHER);
        userRepository.save(teacher);

        // Setup Course
        course = new Course("Course 1", "C1", "Desc", Semester.FIRST, "2024", teacher);
        courseRepository.save(course);
    }

    @Test
    void createProject_Success() {
        CreateProjectRequest req = new CreateProjectRequest();
        req.setName("Project Alpha");
        req.setDescription("Description Alpha");
        req.setCourseId(course.getId());
        req.setStartDate(LocalDate.now());
        req.setEndDate(LocalDate.now().plusDays(7));

        ProjectResponse response = projectService.createProject(req);

        assertNotNull(response.getId());
        assertEquals("Project Alpha", response.getName());
        assertEquals(course.getId(), response.getCourseId());
        assertEquals(ProjectStatus.PLANNING, response.getStatus());
    }

    @Test
    void createProject_CourseNotFound_ThrowsException() {
        CreateProjectRequest req = new CreateProjectRequest();
        req.setName("Project Beta");
        req.setCourseId(999L); // Invalid ID

        assertThrows(ResourceNotFoundException.class, () -> {
            projectService.createProject(req);
        });
    }

    @Test
    void getAllProjects_ExcludesArchived() {
        // Create Active Project
        Project p1 = new Project("P1", "D1", LocalDate.now(), LocalDate.now().plusDays(1), course);
        p1.setStatus(ProjectStatus.ACTIVE);
        projectRepository.save(p1);

        // Create Archived Project
        Project p2 = new Project("P2", "D2", LocalDate.now(), LocalDate.now().plusDays(1), course);
        p2.setStatus(ProjectStatus.ARCHIVED);
        projectRepository.save(p2);

        List<ProjectResponse> projects = projectService.getAllProjects();

        assertEquals(1, projects.size());
        assertEquals("P1", projects.get(0).getName());
    }

    @Test
    void getProjectById_Success() {
        Project p = new Project("Find Me", "Desc", LocalDate.now(), LocalDate.now().plusDays(1), course);
        p = projectRepository.save(p);

        ProjectResponse found = projectService.getProjectById(p.getId());

        assertEquals(p.getId(), found.getId());
        assertEquals("Find Me", found.getName());
    }

    @Test
    void getProjectById_Archived_ThrowsException() {
        Project p = new Project("Archived", "Desc", LocalDate.now(), LocalDate.now().plusDays(1), course);
        p.setStatus(ProjectStatus.ARCHIVED);
        p = projectRepository.save(p);

        Long id = p.getId();
        assertThrows(ResourceNotFoundException.class, () -> {
            projectService.getProjectById(id);
        });
    }

    @Test
    void updateProject_Success() {
        Project p = new Project("Original", "Desc", LocalDate.now(), LocalDate.now().plusDays(1), course);
        p = projectRepository.save(p);

        UpdateProjectRequest req = new UpdateProjectRequest();
        req.setName("Updated Name");
        req.setDescription("Updated Desc");

        ProjectResponse updated = projectService.updateProject(p.getId(), req);

        assertEquals("Updated Name", updated.getName());
        assertEquals("Updated Desc", updated.getDescription());
    }

    @Test
    void deleteProject_ArchivesProject() {
        Project p = new Project("To Delete", "Desc", LocalDate.now(), LocalDate.now().plusDays(1), course);
        p = projectRepository.save(p);

        projectService.deleteProject(p.getId());

        Project saved = projectRepository.findById(p.getId()).orElseThrow();
        assertEquals(ProjectStatus.ARCHIVED, saved.getStatus());
    }

    @Test
    void completeProject_Success() {
        Project p = new Project("To Complete", "Desc", LocalDate.now(), LocalDate.now().plusDays(1), course);
        p.setStatus(ProjectStatus.ACTIVE);
        p = projectRepository.save(p);

        ProjectResponse completed = projectService.completeProject(p.getId());

        assertEquals(ProjectStatus.COMPLETED, completed.getStatus());
    }

    @Test
    void completeProject_AlreadyCompleted_ThrowsException() {
        Project p = new Project("Already Done", "Desc", LocalDate.now(), LocalDate.now().plusDays(1), course);
        p.setStatus(ProjectStatus.COMPLETED);
        p = projectRepository.save(p);

        Long id = p.getId();
        assertThrows(BusinessLogicException.class, () -> {
            projectService.completeProject(id);
        });
    }
}
