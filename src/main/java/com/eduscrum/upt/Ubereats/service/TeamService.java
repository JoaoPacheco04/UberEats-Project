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
            throw new RuntimeException("Team name '" + request.getName() + "' already exists");
        }
        Team team = new Team(request.getName());
        return teamRepository.save(team);
    }

    // Associate a team with a project
    public Team addTeamToProject(Long teamId, Long projectId) {
        Team team = getTeamById(teamId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

        if (team.getProjects().contains(project)) {
            throw new IllegalStateException("Team is already in this project.");
        }

        team.addProject(project);
        return teamRepository.save(team);
    }

    // Add member to team
    public TeamMember addMemberToTeam(Long teamId, AddMemberRequest request) {
        Team team = getTeamById(teamId);
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        // The validation for a user being in multiple teams per project was removed for now.
        // A more complex rule would be needed to check per-project team membership.

        if (request.getRole() == ScrumRole.SCRUM_MASTER || request.getRole() == ScrumRole.PRODUCT_OWNER) {
            boolean roleTaken = teamMemberRepository.findByTeamIdAndRoleAndIsActiveTrue(teamId, request.getRole())
                    .stream()
                    .findFirst()
                    .isPresent();

            if (roleTaken) {
                throw new RuntimeException(request.getRole() + " role is already taken in this team");
            }
        }

        TeamMember member = new TeamMember(team, user, request.getRole());
        return teamMemberRepository.save(member);
    }

    // Get teams for project
    public List<Team> getTeamsByProject(Long projectId) {
        return teamRepository.findByProjects_Id(projectId);
    }

    // Get user's teams
    public List<Team> getUserTeams(Long userId) {
        return teamRepository.findTeamsByUserId(userId);
    }

    // Remove member from team
    public void removeMemberFromTeam(Long teamId, Long userId) {
        TeamMember member = teamMemberRepository.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new RuntimeException("Team member not found"));

        member.leaveTeam();
        teamMemberRepository.save(member);
    }

    // Update member role
    public TeamMember updateMemberRole(Long teamId, Long userId, UpdateMemberRoleRequest request) {
        TeamMember member = teamMemberRepository.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new RuntimeException("Team member not found"));

        if (request.getRole() == ScrumRole.SCRUM_MASTER || request.getRole() == ScrumRole.PRODUCT_OWNER) {
            boolean roleTaken = teamMemberRepository.findByTeamIdAndRoleAndIsActiveTrue(teamId, request.getRole())
                    .stream()
                    .anyMatch(existingMember -> !existingMember.getId().equals(member.getId()));

            if (roleTaken) {
                throw new RuntimeException(request.getRole() + " role is already taken in this team");
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
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + teamId));
    }

    // Delete team (soft delete - deactivate all members)
    public void deleteTeam(Long teamId) {
        Team team = getTeamById(teamId);
        List<TeamMember> members = teamMemberRepository.findByTeamIdAndIsActiveTrue(teamId);

        members.forEach(TeamMember::leaveTeam);
        teamMemberRepository.saveAll(members);
    }

    @Transactional(readOnly = true)
    public Long countCompletedProjectsByTeamInCourse(Long teamId, Long courseId) {
        return teamRepository.countCompletedProjectsByTeamInCourse(teamId, courseId);
    }
}