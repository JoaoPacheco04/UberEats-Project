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
 * @version 0.8.0 (2025-11-20)
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

    /**
     * Creates a new team.
     *
     * @param request The request containing team details
     * @return ResponseEntity containing the created team
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
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

    // Get teams for project
    @GetMapping("/project/{projectId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TeamResponse>> getProjectTeams(@PathVariable Long projectId) {
        List<Team> teams = teamService.getTeamsByProject(projectId);
        List<TeamResponse> response = teams.stream()
                .map(TeamResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Get user's teams
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TeamResponse>> getUserTeams(@PathVariable Long userId) {
        List<Team> teams = teamService.getUserTeams(userId);
        List<TeamResponse> response = teams.stream()
                .map(TeamResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Get team by ID
    @GetMapping("/{teamId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TeamResponse> getTeam(@PathVariable Long teamId) {
        Team team = teamService.getTeamById(teamId);
        return ResponseEntity.ok(new TeamResponse(team));
    }

    // Add member to team
    @PostMapping("/{teamId}/members")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<TeamMemberResponse> addMember(
            @PathVariable Long teamId,
            @Valid @RequestBody AddMemberRequest request) {
        TeamMember member = teamService.addMemberToTeam(teamId, request);
        return ResponseEntity.ok(new TeamMemberResponse(member));
    }

    // Get team members
    @GetMapping("/{teamId}/members")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TeamMemberResponse>> getTeamMembers(@PathVariable Long teamId) {
        List<TeamMember> members = teamService.getTeamMembers(teamId);
        List<TeamMemberResponse> response = members.stream()
                .map(TeamMemberResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Update member role
    @PutMapping("/{teamId}/members/{userId}/role")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<TeamMemberResponse> updateMemberRole(
            @PathVariable Long teamId,
            @PathVariable Long userId,
            @Valid @RequestBody UpdateMemberRoleRequest request) {
        TeamMember member = teamService.updateMemberRole(teamId, userId, request);
        return ResponseEntity.ok(new TeamMemberResponse(member));
    }

    // Remove member from team
    @DeleteMapping("/{teamId}/members/{userId}")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long teamId,
            @PathVariable Long userId) {
        teamService.removeMemberFromTeam(teamId, userId);
        return ResponseEntity.noContent().build();
    }

    // Delete team
    @DeleteMapping("/{teamId}")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long teamId) {
        teamService.deleteTeam(teamId);
        return ResponseEntity.noContent().build();
    }
}
