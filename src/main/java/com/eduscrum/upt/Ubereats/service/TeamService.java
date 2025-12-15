package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.dto.request.AddMemberRequest;
import com.eduscrum.upt.Ubereats.dto.request.CreateTeamRequest;
import com.eduscrum.upt.Ubereats.dto.request.UpdateMemberRoleRequest;
import com.eduscrum.upt.Ubereats.entity.Team;
import com.eduscrum.upt.Ubereats.entity.TeamMember;
import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.entity.Project;
import com.eduscrum.upt.Ubereats.entity.enums.ScrumRole;
import com.eduscrum.upt.Ubereats.repository.TeamRepository;
import com.eduscrum.upt.Ubereats.repository.TeamMemberRepository;
import com.eduscrum.upt.Ubereats.repository.ProjectRepository;
import com.eduscrum.upt.Ubereats.repository.UserRepository;
import com.eduscrum.upt.Ubereats.exception.ResourceNotFoundException;
import com.eduscrum.upt.Ubereats.exception.BusinessLogicException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing teams and team memberships in the EduScrum
 * platform.
 * Handles team creation, member management, project associations, and role
 * assignments.
 *
 * @author Bruna
 * @author Ana
 * @version 0.5.0 (2025-11-05)
 */
@Service
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    /**
     * Constructs a new TeamService with required dependencies.
     *
     * @param teamRepository       Repository for team data access
     * @param teamMemberRepository Repository for team member data access
     * @param projectRepository    Repository for project data access
     * @param userRepository       Repository for user data access
     */
    public TeamService(TeamRepository teamRepository,
            TeamMemberRepository teamMemberRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new team with the specified name and optionally associates it with
     * a project.
     *
     * @param request The request containing team name and optional project ID
     * @return The newly created Team entity
     * @throws BusinessLogicException    if a team with the same name already exists
     * @throws ResourceNotFoundException if the specified project is not found
     */
    public Team createTeam(CreateTeamRequest request) {
        if (teamRepository.existsByName(request.getName())) {
            throw new BusinessLogicException("Team name '" + request.getName() + "' already exists");
        }
        Team team = new Team(request.getName());
        Team savedTeam = teamRepository.save(team);

        if (request.getProjectId() != null) {
            Project project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Project not found with id: " + request.getProjectId()));

            // Check if project already has a team
            if (project.getTeam() != null) {
                throw new BusinessLogicException(
                        "Project already has a team assigned. Remove the existing team first.");
            }

            project.setTeam(savedTeam);
            projectRepository.save(project);
        }

        return savedTeam;
    }

    /**
     * Associates an existing team with a project.
     *
     * @param teamId    The ID of the team to associate
     * @param projectId The ID of the project to associate with
     * @return The updated Team entity
     * @throws ResourceNotFoundException if team or project is not found
     * @throws BusinessLogicException    if team is already in the project
     */
    public Team addTeamToProject(Long teamId, Long projectId) {
        Team team = getTeamById(teamId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        // Check if project already has a team
        if (project.getTeam() != null) {
            if (project.getTeam().getId().equals(teamId)) {
                throw new BusinessLogicException("This team is already assigned to this project.");
            }
            throw new BusinessLogicException("Project already has a team assigned. Remove the existing team first.");
        }

        project.setTeam(team);
        projectRepository.save(project);
        return team;
    }

    /**
     * Adds a user as a member to a team with the specified Scrum role.
     * Ensures that SCRUM_MASTER and PRODUCT_OWNER roles are unique within a team.
     *
     * @param teamId  The ID of the team
     * @param request The request containing user ID and role
     * @return The newly created TeamMember entity
     * @throws ResourceNotFoundException if team or user is not found
     * @throws BusinessLogicException    if the specified role is already taken
     */
    public TeamMember addMemberToTeam(Long teamId, AddMemberRequest request) {
        Team team = getTeamById(teamId);
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        if (request.getRole() == ScrumRole.SCRUM_MASTER || request.getRole() == ScrumRole.PRODUCT_OWNER) {
            boolean roleTaken = teamMemberRepository.findByTeamIdAndRoleAndIsActiveTrue(teamId, request.getRole())
                    .stream()
                    .findFirst()
                    .isPresent();

            if (roleTaken) {
                throw new BusinessLogicException(request.getRole() + " role is already taken in this team");
            }
        }

        TeamMember member = new TeamMember(team, user, request.getRole());
        return teamMemberRepository.save(member);
    }

    // Get team for project (single team per project)
    public Team getTeamByProject(Long projectId) {
        return teamRepository.findByProjectId(projectId).orElse(null);
    }

    /**
     * Retrieves all teams that a user is a member of.
     *
     * @param userId The ID of the user
     * @return List of teams the user belongs to
     */
    public List<Team> getUserTeams(Long userId) {
        return teamRepository.findTeamsByUserId(userId);
    }

    /**
     * Removes a user from a team by deactivating their membership.
     *
     * @param teamId The ID of the team
     * @param userId The ID of the user to remove
     * @throws ResourceNotFoundException if the team member is not found
     */
    public void removeMemberFromTeam(Long teamId, Long userId) {
        TeamMember member = teamMemberRepository.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team member not found"));

        member.leaveTeam();
        teamMemberRepository.save(member);
    }

    /**
     * Updates the Scrum role of a team member.
     * Ensures that SCRUM_MASTER and PRODUCT_OWNER roles remain unique.
     *
     * @param teamId  The ID of the team
     * @param userId  The ID of the user whose role is being updated
     * @param request The request containing the new role
     * @return The updated TeamMember entity
     * @throws ResourceNotFoundException if the team member is not found
     * @throws BusinessLogicException    if the new role is already taken
     */
    public TeamMember updateMemberRole(Long teamId, Long userId, UpdateMemberRoleRequest request) {
        TeamMember member = teamMemberRepository.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team member not found"));

        if (request.getRole() == ScrumRole.SCRUM_MASTER || request.getRole() == ScrumRole.PRODUCT_OWNER) {
            boolean roleTaken = teamMemberRepository.findByTeamIdAndRoleAndIsActiveTrue(teamId, request.getRole())
                    .stream()
                    .anyMatch(existingMember -> !existingMember.getId().equals(member.getId()));

            if (roleTaken) {
                throw new BusinessLogicException(request.getRole() + " role is already taken in this team");
            }
        }

        member.setRole(request.getRole());
        return teamMemberRepository.save(member);
    }

    /**
     * Retrieves all active members of a team.
     *
     * @param teamId The ID of the team
     * @return List of active team members
     */
    public List<TeamMember> getTeamMembers(Long teamId) {
        return teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId);
    }

    /**
     * Retrieves a team by its ID.
     *
     * @param teamId The ID of the team to retrieve
     * @return The Team entity
     * @throws ResourceNotFoundException if the team is not found
     */
    public Team getTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
    }

    /**
     * Deletes a team by deactivating all its members (soft delete).
     *
     * @param teamId The ID of the team to delete
     * @throws ResourceNotFoundException if the team is not found
     */
    public void deleteTeam(Long teamId) {
        Team team = getTeamById(teamId);
        List<TeamMember> members = teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId);

        members.forEach(TeamMember::leaveTeam);
        teamMemberRepository.saveAll(members);
    }

    /**
     * Closes all team memberships, typically used when a project is completed.
     *
     * @param teamId The ID of the team
     */
    public void closeTeamMemberships(Long teamId) {
        List<TeamMember> members = teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId);
        members.forEach(TeamMember::leaveTeam);
        teamMemberRepository.saveAll(members);
    }

    /**
     * Counts the number of completed projects for a team within a specific course.
     *
     * @param teamId   The ID of the team
     * @param courseId The ID of the course
     * @return The count of completed projects
     */
    @Transactional(readOnly = true)
    public Long countCompletedProjectsByTeamInCourse(Long teamId, Long courseId) {
        return teamRepository.countCompletedProjectsByTeamInCourse(teamId, courseId);
    }
}
