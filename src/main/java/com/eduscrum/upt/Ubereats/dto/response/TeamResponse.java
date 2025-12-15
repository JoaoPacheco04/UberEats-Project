package com.eduscrum.upt.Ubereats.dto.response;

import com.eduscrum.upt.Ubereats.entity.Team;
import com.eduscrum.upt.Ubereats.entity.Project;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for team response data.
 * Contains team details, member counts, and performance metrics.
 *
 * @author Joao
 * @author Ana
 * @version 0.9.1
 */
public class TeamResponse {
    private Long id;
    private String name;
    private Integer projectCount;
    private List<String> projectNames;
    private Integer memberCount;
    private Integer totalPoints;
    private String scrumMaster;
    private String productOwner;
    private BigDecimal currentProgress;
    private BigDecimal performanceRating;
    private LocalDateTime createdAt;

    /**
     * Constructs a TeamResponse from a Team entity.
     *
     * @param team The team entity to convert
     */
    public TeamResponse(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.projectCount = team.getProjects().size();
        this.projectNames = team.getProjects().stream().map(Project::getName).collect(Collectors.toList());
        this.memberCount = team.getMemberCount();
        this.totalPoints = team.getTotalPoints();
        this.scrumMaster = team.getScrumMaster() != null ? team.getScrumMaster().getFullName() : "Not assigned";
        this.productOwner = team.getProductOwner() != null ? team.getProductOwner().getFullName() : "Not assigned";
        this.currentProgress = team.getCurrentProgress();
        this.performanceRating = team.getPerformanceRating();
        this.createdAt = team.getCreatedAt();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getProjectCount() {
        return projectCount;
    }

    public List<String> getProjectNames() {
        return projectNames;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }

    public String getScrumMaster() {
        return scrumMaster;
    }

    public String getProductOwner() {
        return productOwner;
    }

    public BigDecimal getCurrentProgress() {
        return currentProgress;
    }

    public BigDecimal getPerformanceRating() {
        return performanceRating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
