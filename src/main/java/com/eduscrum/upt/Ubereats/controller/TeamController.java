package com.eduscrum.upt.Ubereats.controller;

import com.eduscrum.upt.Ubereats.dto.request.AddMemberRequest;
import com.eduscrum.upt.Ubereats.dto.request.CreateTeamRequest;
import com.eduscrum.upt.Ubereats.dto.request.UpdateMemberRoleRequest;
import com.eduscrum.upt.Ubereats.dto.response.TeamMemberResponse;
import com.eduscrum.upt.Ubereats.dto.response.TeamResponse;
import com.eduscrum.upt.Ubereats.entity.Team;
import com.eduscrum.upt.Ubereats.entity.TeamMember;
import com.eduscrum.upt.Ubereats.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing teams and team memberships.
 * Provides endpoints for team CRUD operations and member management.
 *
 * @author Joao
 * @author Ana
 * @version 0.8.0
 */
@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    /**
     * Constructs a new TeamController with required dependencies.
     *
     * @param teamService Service for team operations
     */
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    // Create new team (teachers and students can create teams)
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_STUDENT')")
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody CreateTeamRequest request) {
        Team team = teamService.createTeam(request);
        return ResponseEntity.ok(new TeamResponse(team));
    }

    /**
     * Associates a team with a project.
     *
     * @param teamId    The ID of the team
     * @param projectId The ID of the project
     * @return ResponseEntity containing the updated team
     */
    @PostMapping("/{teamId}/projects/{projectId}")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<TeamResponse> addTeamToProject(@PathVariable Long teamId, @PathVariable Long projectId) {
        Team team = teamService.addTeamToProject(teamId, projectId);
        return ResponseEntity.ok(new TeamResponse(team));
    }

    // Get team for project (single team per project)
    @GetMapping("/project/{projectId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TeamResponse> getProjectTeam(@PathVariable Long projectId) {
        Team team = teamService.getTeamByProject(projectId);
        if (team == null) {
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(new TeamResponse(team));
    }

    /**
     * Retrieves all teams for a specific user.
     *
     * @param userId The ID of the user
     * @return ResponseEntity containing the list of teams
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TeamResponse>> getUserTeams(@PathVariable Long userId) {
        List<Team> teams = teamService.getUserTeams(userId);
        List<TeamResponse> response = teams.stream()
                .map(TeamResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a team by its ID.
     *
     * @param teamId The ID of the team
     * @return ResponseEntity containing the team
     */
    @GetMapping("/{teamId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TeamResponse> getTeam(@PathVariable Long teamId) {
        Team team = teamService.getTeamById(teamId);
        return ResponseEntity.ok(new TeamResponse(team));
    }

    // Add member to team (teachers and students can add members)
    @PostMapping("/{teamId}/members")
    @PreAuthorize("hasAnyAuthority('ROLE_TEACHER', 'ROLE_STUDENT')")
    public ResponseEntity<TeamMemberResponse> addMember(
            @PathVariable Long teamId,
            @Valid @RequestBody AddMemberRequest request) {
        TeamMember member = teamService.addMemberToTeam(teamId, request);
        return ResponseEntity.ok(new TeamMemberResponse(member));
    }

    /**
     * Retrieves all members of a team.
     *
     * @param teamId The ID of the team
     * @return ResponseEntity containing the list of team members
     */
    @GetMapping("/{teamId}/members")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TeamMemberResponse>> getTeamMembers(@PathVariable Long teamId) {
        List<TeamMember> members = teamService.getTeamMembers(teamId);
        List<TeamMemberResponse> response = members.stream()
                .map(TeamMemberResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Updates the role of a team member.
     *
     * @param teamId  The ID of the team
     * @param userId  The ID of the user (member)
     * @param request The request containing the new role
     * @return ResponseEntity containing the updated team member
     */
    @PutMapping("/{teamId}/members/{userId}/role")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<TeamMemberResponse> updateMemberRole(
            @PathVariable Long teamId,
            @PathVariable Long userId,
            @Valid @RequestBody UpdateMemberRoleRequest request) {
        TeamMember member = teamService.updateMemberRole(teamId, userId, request);
        return ResponseEntity.ok(new TeamMemberResponse(member));
    }

    /**
     * Removes a member from a team.
     *
     * @param teamId The ID of the team
     * @param userId The ID of the user to remove
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{teamId}/members/{userId}")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long teamId,
            @PathVariable Long userId) {
        teamService.removeMemberFromTeam(teamId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes a team.
     *
     * @param teamId The ID of the team to delete
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{teamId}")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long teamId) {
        teamService.deleteTeam(teamId);
        return ResponseEntity.noContent().build();
    }
}
