package com.eduscrum.upt.Ubereats.dto.response;

import com.eduscrum.upt.Ubereats.entity.Team;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
    * DTO for team response.
    * Includes team details and statistics.
 */
public class TeamResponse {
    private Long id;
    private String name;
    private String projectName;
    private Long projectId;
    private String courseName;
    private Integer memberCount;
    private Integer totalPoints;
    private String scrumMaster;
    private String productOwner;
    private BigDecimal currentProgress;
    private BigDecimal performanceRating;
    private LocalDateTime createdAt;

    // Constructor from Team entity
    public TeamResponse(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.projectName = team.getProject().getName();
        this.projectId = team.getProject().getId();
        this.courseName = team.getProject().getCourse().getName();
        this.memberCount = team.getMemberCount();
        this.totalPoints = team.getTotalPoints();
        this.scrumMaster = team.getScrumMaster() != null ? team.getScrumMaster().getFullName() : "Not assigned";
        this.productOwner = team.getProductOwner() != null ? team.getProductOwner().getFullName() : "Not assigned";
        this.currentProgress = team.getCurrentProgress();
        this.performanceRating = team.getPerformanceRating();
        this.createdAt = team.getCreatedAt();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getProjectName() { return projectName; }
    public Long getProjectId() { return projectId; }
    public String getCourseName() { return courseName; }
    public Integer getMemberCount() { return memberCount; }
    public Integer getTotalPoints() { return totalPoints; }
    public String getScrumMaster() { return scrumMaster; }
    public String getProductOwner() { return productOwner; }
    public BigDecimal getCurrentProgress() { return currentProgress; }
    public BigDecimal getPerformanceRating() { return performanceRating; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}