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

@Service
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public TeamService(TeamRepository teamRepository,
            TeamMemberRepository teamMemberRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    // Create new team
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

    // Associate a team with a project
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

    // Add member to team
    public TeamMember addMemberToTeam(Long teamId, AddMemberRequest request) {
        Team team = getTeamById(teamId);
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        // The validation for a user being in multiple teams per project was removed for
        // now.
        // A more complex rule would be needed to check per-project team membership.

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

    // Get user's teams
    public List<Team> getUserTeams(Long userId) {
        return teamRepository.findTeamsByUserId(userId);
    }

    // Remove member from team
    public void removeMemberFromTeam(Long teamId, Long userId) {
        TeamMember member = teamMemberRepository.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team member not found"));

        member.leaveTeam();
        teamMemberRepository.save(member);
    }

    // Update member role
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

    // Get team members
    public List<TeamMember> getTeamMembers(Long teamId) {
        return teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId);
    }

    // Get team by ID
    public Team getTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
    }

    // Delete team (soft delete - deactivate all members)
    public void deleteTeam(Long teamId) {
        Team team = getTeamById(teamId);
        List<TeamMember> members = teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId);

        members.forEach(TeamMember::leaveTeam);
        teamMemberRepository.saveAll(members);
    }

    // Close all team memberships (for project completion)
    public void closeTeamMemberships(Long teamId) {
        List<TeamMember> members = teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId);
        members.forEach(TeamMember::leaveTeam);
        teamMemberRepository.saveAll(members);
    }

    @Transactional(readOnly = true)
    public Long countCompletedProjectsByTeamInCourse(Long teamId, Long courseId) {
        return teamRepository.countCompletedProjectsByTeamInCourse(teamId, courseId);
    }
}