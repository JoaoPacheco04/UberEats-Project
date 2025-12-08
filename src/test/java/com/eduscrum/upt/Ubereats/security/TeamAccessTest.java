package com.eduscrum.upt.Ubereats.security;

import com.eduscrum.upt.Ubereats.dto.request.CreateTeamRequest;
import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.entity.enums.UserRole;
import com.eduscrum.upt.Ubereats.entity.enums.Semester;
import com.eduscrum.upt.Ubereats.entity.Course;
import com.eduscrum.upt.Ubereats.entity.Project;
import com.eduscrum.upt.Ubereats.repository.CourseRepository;
import com.eduscrum.upt.Ubereats.repository.ProjectRepository;
import com.eduscrum.upt.Ubereats.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.jdbc.core.JdbcTemplate;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TeamAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    private String teacherToken;
    private Long testProjectId;

    @BeforeEach
    void setUp() {
        // Fix DB Schema issues (legacy column not nullable)
        try {
            jdbcTemplate.execute("ALTER TABLE teams MODIFY COLUMN project_id BIGINT NULL");
        } catch (Exception e) {
            // Ignore if fails (e.g. column doesn't exist or other issue), let test proceed
            // to fail naturally
            System.out.println("Warning: Could not alter table: " + e.getMessage());
        }

        // Create a teacher user
        String email = "teacher_test_" + System.currentTimeMillis() + "@test.com";
        User teacher = new User(
                "teacher_user_" + System.currentTimeMillis(),
                email,
                passwordEncoder.encode("password"),
                UserRole.TEACHER,
                "Test",
                "Teacher");
        userRepository.save(teacher);

        // Generate token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, "password"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        teacherToken = jwtTokenProvider.generateToken(authentication);

        // Create a dummy course and project
        Course course = new Course("Test Course", "TEST101", "Description", Semester.FIRST, "2024-2025", teacher);
        courseRepository.save(course);

        Project project = new Project("Test Project", "Desc", LocalDate.now(), LocalDate.now().plusDays(10), course);
        projectRepository.save(project);
        testProjectId = project.getId();
    }

    @Test
    void createTeam_withTeacherRole_shouldNotReturn403() throws Exception {
        CreateTeamRequest request = new CreateTeamRequest();
        request.setName("Team Alpha Test");
        request.setProjectId(testProjectId);

        mockMvc.perform(post("/api/teams")
                .header("Authorization", "Bearer " + teacherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 403) {
                        throw new AssertionError("Returned 403 Forbidden! Security configuration is blocking access.");
                    }
                    // We don't care if it's 200, 400, or 404, as long as it's NOT 403
                });
    }
}
