package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.dto.request.AddMemberRequest;
import com.eduscrum.upt.Ubereats.dto.request.CreateTeamRequest;
import com.eduscrum.upt.Ubereats.dto.request.UpdateMemberRoleRequest;
import com.eduscrum.upt.Ubereats.entity.Course;
import com.eduscrum.upt.Ubereats.entity.Project;
import com.eduscrum.upt.Ubereats.entity.Team;
import com.eduscrum.upt.Ubereats.entity.TeamMember;
import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.entity.enums.Semester;
import com.eduscrum.upt.Ubereats.entity.enums.ScrumRole;
import com.eduscrum.upt.Ubereats.entity.enums.UserRole;
import com.eduscrum.upt.Ubereats.exception.BusinessLogicException;
import com.eduscrum.upt.Ubereats.exception.ResourceNotFoundException;
import com.eduscrum.upt.Ubereats.repository.CourseRepository;
import com.eduscrum.upt.Ubereats.repository.ProjectRepository;
import com.eduscrum.upt.Ubereats.repository.TeamMemberRepository;
import com.eduscrum.upt.Ubereats.repository.TeamRepository;
import com.eduscrum.upt.Ubereats.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for TeamService.
 *
 * @author UberEats
 * @version 0.8.1
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TeamServiceTest {

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    private Project project;
    private User teacher;
    private User student1;
    private User student2;
    private Course course;

    @BeforeEach
    void setUp() {
        teamMemberRepository.deleteAll();
        teamRepository.deleteAll();
        projectRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();

        // Create Teacher
        teacher = new User();
        teacher.setFirstName("Prof");
        teacher.setLastName("T");
        teacher.setEmail("prof@team.com");
        teacher.setUsername("profteam");
        teacher.setPassword("pass");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        // Create Students
        student1 = new User();
        student1.setFirstName("Student");
        student1.setLastName("One");
        student1.setEmail("s1@team.com");
        student1.setUsername("student1");
        student1.setPassword("pass");
        student1.setRole(UserRole.STUDENT);
        student1 = userRepository.save(student1);

        student2 = new User();
        student2.setFirstName("Student");
        student2.setLastName("Two");
        student2.setEmail("s2@team.com");
        student2.setUsername("student2");
        student2.setPassword("pass");
        student2.setRole(UserRole.STUDENT);
        student2 = userRepository.save(student2);

        // Create Course
        course = new Course("TeamCourse", "TC1", "Desc", Semester.FIRST, "2024", teacher);
        course = courseRepository.save(course);

        // Create Project
        project = new Project("Team Project", "Desc", LocalDate.now(), LocalDate.now().plusDays(10), course);
        project = projectRepository.save(project);
    }

    // ===================== CREATE TEAM TESTS =====================

    @Test
    void createTeam_Success_NoProject() {
        CreateTeamRequest request = new CreateTeamRequest();
        request.setName("Alpha Team");

        Team created = teamService.createTeam(request);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Alpha Team", created.getName());
        assertTrue(teamRepository.existsByName("Alpha Team"));
    }

    @Test
    void createTeam_NameDuplicate_ThrowsException() {
        // Create first team
        CreateTeamRequest request1 = new CreateTeamRequest();
        request1.setName("Duplicate Team");
        teamService.createTeam(request1);

        // Try to create duplicate
        CreateTeamRequest request2 = new CreateTeamRequest();
        request2.setName("Duplicate Team");

        assertThrows(BusinessLogicException.class, () -> {
            teamService.createTeam(request2);
        });
    }

    @Test
    void createTeam_WithProject_Success() {
        CreateTeamRequest request = new CreateTeamRequest();
        request.setName("Beta Team");
        request.setProjectId(project.getId());

        Team created = teamService.createTeam(request);

        assertNotNull(created);
        // Verify project has the team assigned
        Project updatedProject = projectRepository.findById(project.getId()).get();
        assertNotNull(updatedProject.getTeam());
        assertEquals(created.getId(), updatedProject.getTeam().getId());
    }

    @Test
    void createTeam_WithInvalidProject_ThrowsException() {
        CreateTeamRequest request = new CreateTeamRequest();
        request.setName("Invalid Project Team");
        request.setProjectId(999L);

        assertThrows(ResourceNotFoundException.class, () -> {
            teamService.createTeam(request);
        });
    }

    // ===================== GET TEAM BY ID TESTS =====================

    @Test
    void getTeamById_Success() {
        CreateTeamRequest request = new CreateTeamRequest();
        request.setName("Gamma Team");
        Team created = teamService.createTeam(request);

        Team found = teamService.getTeamById(created.getId());

        assertEquals("Gamma Team", found.getName());
    }

    @Test
    void getTeamById_NotFound_ThrowsException() {
        assertThrows(ResourceNotFoundException.class, () -> {
            teamService.getTeamById(999L);
        });
    }

    // ===================== ADD TEAM TO PROJECT TESTS =====================

    @Test
    void addTeamToProject_Success() {
        CreateTeamRequest request = new CreateTeamRequest();
        request.setName("Project Bound Team");
        Team team = teamService.createTeam(request);

        teamService.addTeamToProject(team.getId(), project.getId());

        // Verify team is assigned to project
        Project updatedProject = projectRepository.findById(project.getId()).get();
        assertNotNull(updatedProject.getTeam());
        assertEquals(team.getId(), updatedProject.getTeam().getId());
    }

    @Test
    void addTeamToProject_TeamAlreadyInProject_ThrowsException() {
        CreateTeamRequest request = new CreateTeamRequest();
        request.setName("Already In Project Team");
        request.setProjectId(project.getId());
        Team team = teamService.createTeam(request);

        assertThrows(BusinessLogicException.class, () -> {
            teamService.addTeamToProject(team.getId(), project.getId());
        });
    }

    @Test
    void addTeamToProject_ProjectNotFound_ThrowsException() {
        CreateTeamRequest request = new CreateTeamRequest();
        request.setName("Orphan Team");
        Team team = teamService.createTeam(request);

        assertThrows(ResourceNotFoundException.class, () -> {
            teamService.addTeamToProject(team.getId(), 999L);
        });
    }

    // ===================== ADD MEMBER TO TEAM TESTS =====================

    @Test
    void addMemberToTeam_AsDeveloper_Success() {
        CreateTeamRequest teamRequest = new CreateTeamRequest();
        teamRequest.setName("Dev Team");
        Team team = teamService.createTeam(teamRequest);

        AddMemberRequest memberRequest = new AddMemberRequest();
        memberRequest.setUserId(student1.getId());
        memberRequest.setRole(ScrumRole.DEVELOPER);

        TeamMember member = teamService.addMemberToTeam(team.getId(), memberRequest);

        assertNotNull(member.getId());
        assertEquals(ScrumRole.DEVELOPER, member.getRole());
        assertTrue(member.getIsActive());
    }

    @Test
    void addMemberToTeam_AsScrumMaster_Success() {
        CreateTeamRequest teamRequest = new CreateTeamRequest();
        teamRequest.setName("SM Team");
        Team team = teamService.createTeam(teamRequest);

        AddMemberRequest memberRequest = new AddMemberRequest();
        memberRequest.setUserId(student1.getId());
        memberRequest.setRole(ScrumRole.SCRUM_MASTER);

        TeamMember member = teamService.addMemberToTeam(team.getId(), memberRequest);

        assertEquals(ScrumRole.SCRUM_MASTER, member.getRole());
    }

    @Test
    void addMemberToTeam_DuplicateScrumMaster_ThrowsException() {
        CreateTeamRequest teamRequest = new CreateTeamRequest();
        teamRequest.setName("Dup SM Team");
        Team team = teamService.createTeam(teamRequest);

        // Add first Scrum Master
        AddMemberRequest sm1 = new AddMemberRequest();
        sm1.setUserId(student1.getId());
        sm1.setRole(ScrumRole.SCRUM_MASTER);
        teamService.addMemberToTeam(team.getId(), sm1);

        // Try to add second Scrum Master
        AddMemberRequest sm2 = new AddMemberRequest();
        sm2.setUserId(student2.getId());
        sm2.setRole(ScrumRole.SCRUM_MASTER);

        assertThrows(BusinessLogicException.class, () -> {
            teamService.addMemberToTeam(team.getId(), sm2);
        });
    }

    @Test
    void addMemberToTeam_DuplicateProductOwner_ThrowsException() {
        CreateTeamRequest teamRequest = new CreateTeamRequest();
        teamRequest.setName("Dup PO Team");
        Team team = teamService.createTeam(teamRequest);

        // Add first Product Owner
        AddMemberRequest po1 = new AddMemberRequest();
        po1.setUserId(student1.getId());
        po1.setRole(ScrumRole.PRODUCT_OWNER);
        teamService.addMemberToTeam(team.getId(), po1);

        // Try to add second Product Owner
        AddMemberRequest po2 = new AddMemberRequest();
        po2.setUserId(student2.getId());
        po2.setRole(ScrumRole.PRODUCT_OWNER);

        assertThrows(BusinessLogicException.class, () -> {
            teamService.addMemberToTeam(team.getId(), po2);
        });
    }

    @Test
    void addMemberToTeam_UserNotFound_ThrowsException() {
        CreateTeamRequest teamRequest = new CreateTeamRequest();
        teamRequest.setName("Invalid User Team");
        Team team = teamService.createTeam(teamRequest);

        AddMemberRequest memberRequest = new AddMemberRequest();
        memberRequest.setUserId(999L);
        memberRequest.setRole(ScrumRole.DEVELOPER);

        assertThrows(ResourceNotFoundException.class, () -> {
            teamService.addMemberToTeam(team.getId(), memberRequest);
        });
    }

    // ===================== GET TEAM MEMBERS TESTS =====================

    @Test
    void getTeamMembers_ReturnsActiveMembers() {
        CreateTeamRequest teamRequest = new CreateTeamRequest();
        teamRequest.setName("Members Team");
        Team team = teamService.createTeam(teamRequest);

        AddMemberRequest member1 = new AddMemberRequest();
        member1.setUserId(student1.getId());
        member1.setRole(ScrumRole.DEVELOPER);
        teamService.addMemberToTeam(team.getId(), member1);

        AddMemberRequest member2 = new AddMemberRequest();
        member2.setUserId(student2.getId());
        member2.setRole(ScrumRole.DEVELOPER);
        teamService.addMemberToTeam(team.getId(), member2);

        List<TeamMember> members = teamService.getTeamMembers(team.getId());

        assertEquals(2, members.size());
    }

    // ===================== REMOVE MEMBER FROM TEAM TESTS =====================

    @Test
    void removeMemberFromTeam_Success() {
        CreateTeamRequest teamRequest = new CreateTeamRequest();
        teamRequest.setName("Remove Team");
        Team team = teamService.createTeam(teamRequest);

        AddMemberRequest memberRequest = new AddMemberRequest();
        memberRequest.setUserId(student1.getId());
        memberRequest.setRole(ScrumRole.DEVELOPER);
        teamService.addMemberToTeam(team.getId(), memberRequest);

        teamService.removeMemberFromTeam(team.getId(), student1.getId());

        List<TeamMember> members = teamService.getTeamMembers(team.getId());
        assertEquals(0, members.size());
    }

    @Test
    void removeMemberFromTeam_NotFound_ThrowsException() {
        CreateTeamRequest teamRequest = new CreateTeamRequest();
        teamRequest.setName("No Member Team");
        Team team = teamService.createTeam(teamRequest);

        assertThrows(ResourceNotFoundException.class, () -> {
            teamService.removeMemberFromTeam(team.getId(), 999L);
        });
    }

    // ===================== UPDATE MEMBER ROLE TESTS =====================

    @Test
    void updateMemberRole_Success() {
        CreateTeamRequest teamRequest = new CreateTeamRequest();
        teamRequest.setName("Role Update Team");
        Team team = teamService.createTeam(teamRequest);

        AddMemberRequest memberRequest = new AddMemberRequest();
        memberRequest.setUserId(student1.getId());
        memberRequest.setRole(ScrumRole.DEVELOPER);
        teamService.addMemberToTeam(team.getId(), memberRequest);

        UpdateMemberRoleRequest roleRequest = new UpdateMemberRoleRequest();
        roleRequest.setRole(ScrumRole.SCRUM_MASTER);

        TeamMember updated = teamService.updateMemberRole(team.getId(), student1.getId(), roleRequest);

        assertEquals(ScrumRole.SCRUM_MASTER, updated.getRole());
    }

    @Test
    void updateMemberRole_ToTakenScrumMaster_ThrowsException() {
        CreateTeamRequest teamRequest = new CreateTeamRequest();
        teamRequest.setName("Role Conflict Team");
        Team team = teamService.createTeam(teamRequest);

        // Add Scrum Master
        AddMemberRequest sm = new AddMemberRequest();
        sm.setUserId(student1.getId());
        sm.setRole(ScrumRole.SCRUM_MASTER);
        teamService.addMemberToTeam(team.getId(), sm);

        // Add Developer
        AddMemberRequest dev = new AddMemberRequest();
        dev.setUserId(student2.getId());
        dev.setRole(ScrumRole.DEVELOPER);
        teamService.addMemberToTeam(team.getId(), dev);

        // Try to update Developer to Scrum Master
        UpdateMemberRoleRequest roleRequest = new UpdateMemberRoleRequest();
        roleRequest.setRole(ScrumRole.SCRUM_MASTER);

        assertThrows(BusinessLogicException.class, () -> {
            teamService.updateMemberRole(team.getId(), student2.getId(), roleRequest);
        });
    }

    // ===================== GET TEAM BY PROJECT TESTS =====================

    @Test
    void getTeamByProject_ReturnsCorrectTeam() {
        CreateTeamRequest request = new CreateTeamRequest();
        request.setName("Project Team");
        request.setProjectId(project.getId());
        Team created = teamService.createTeam(request);

        Team projectTeam = teamService.getTeamByProject(project.getId());

        assertNotNull(projectTeam);
        assertEquals(created.getId(), projectTeam.getId());
    }

    @Test
    void getTeamByProject_NoTeamAssigned_ReturnsNull() {
        Team projectTeam = teamService.getTeamByProject(project.getId());
        assertNull(projectTeam);
    }

    // ===================== GET USER TEAMS TESTS =====================

    @Test
    void getUserTeams_ReturnsUserTeams() {
        CreateTeamRequest teamRequest = new CreateTeamRequest();
        teamRequest.setName("User Team");
        Team team = teamService.createTeam(teamRequest);

        AddMemberRequest memberRequest = new AddMemberRequest();
        memberRequest.setUserId(student1.getId());
        memberRequest.setRole(ScrumRole.DEVELOPER);
        teamService.addMemberToTeam(team.getId(), memberRequest);

        List<Team> userTeams = teamService.getUserTeams(student1.getId());

        assertEquals(1, userTeams.size());
        assertEquals("User Team", userTeams.get(0).getName());
    }

    // ===================== DELETE TEAM TESTS =====================

    @Test
    void deleteTeam_DeactivatesAllMembers() {
        CreateTeamRequest teamRequest = new CreateTeamRequest();
        teamRequest.setName("Delete Team");
        Team team = teamService.createTeam(teamRequest);

        AddMemberRequest member1 = new AddMemberRequest();
        member1.setUserId(student1.getId());
        member1.setRole(ScrumRole.DEVELOPER);
        teamService.addMemberToTeam(team.getId(), member1);

        AddMemberRequest member2 = new AddMemberRequest();
        member2.setUserId(student2.getId());
        member2.setRole(ScrumRole.DEVELOPER);
        teamService.addMemberToTeam(team.getId(), member2);

        teamService.deleteTeam(team.getId());

        List<TeamMember> activeMembers = teamService.getTeamMembers(team.getId());
        assertEquals(0, activeMembers.size());
    }

    // ===================== CLOSE TEAM MEMBERSHIPS TESTS =====================

    @Test
    void closeTeamMemberships_DeactivatesMembers() {
        CreateTeamRequest teamRequest = new CreateTeamRequest();
        teamRequest.setName("Close Team");
        Team team = teamService.createTeam(teamRequest);

        AddMemberRequest memberRequest = new AddMemberRequest();
        memberRequest.setUserId(student1.getId());
        memberRequest.setRole(ScrumRole.DEVELOPER);
        teamService.addMemberToTeam(team.getId(), memberRequest);

        teamService.closeTeamMemberships(team.getId());

        List<TeamMember> activeMembers = teamService.getTeamMembers(team.getId());
        assertEquals(0, activeMembers.size());
    }
}
