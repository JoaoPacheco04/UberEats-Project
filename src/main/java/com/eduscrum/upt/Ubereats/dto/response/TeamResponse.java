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
 * @author UberEats
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

<<<<<<< HEAD
    /** @return The team ID */
=======
    // Getters
>>>>>>> Yesh_Branch
    public Long getId() {
        return id;
    }

<<<<<<< HEAD
    /** @return The team name */
=======
>>>>>>> Yesh_Branch
    public String getName() {
        return name;
    }

<<<<<<< HEAD
    /** @return The project count */
=======
>>>>>>> Yesh_Branch
    public Integer getProjectCount() {
        return projectCount;
    }

<<<<<<< HEAD
    /** @return The project names */
=======
>>>>>>> Yesh_Branch
    public List<String> getProjectNames() {
        return projectNames;
    }

<<<<<<< HEAD
    /** @return The member count */
=======
>>>>>>> Yesh_Branch
    public Integer getMemberCount() {
        return memberCount;
    }

<<<<<<< HEAD
    /** @return The total points */
=======
>>>>>>> Yesh_Branch
    public Integer getTotalPoints() {
        return totalPoints;
    }

<<<<<<< HEAD
    /** @return The Scrum Master name */
=======
>>>>>>> Yesh_Branch
    public String getScrumMaster() {
        return scrumMaster;
    }

<<<<<<< HEAD
    /** @return The Product Owner name */
=======
>>>>>>> Yesh_Branch
    public String getProductOwner() {
        return productOwner;
    }

<<<<<<< HEAD
    /** @return The current progress */
=======
>>>>>>> Yesh_Branch
    public BigDecimal getCurrentProgress() {
        return currentProgress;
    }

<<<<<<< HEAD
    /** @return The performance rating */
=======
>>>>>>> Yesh_Branch
    public BigDecimal getPerformanceRating() {
        return performanceRating;
    }

<<<<<<< HEAD
    /** @return The creation timestamp */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
=======
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
>>>>>>> Yesh_Branch
