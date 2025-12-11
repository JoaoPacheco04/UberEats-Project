package com.eduscrum.upt.Ubereats.dto.response;

import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for student dashboard statistics.
 * Contains global score, course average, velocity, and badges.
 *
 * @author UberEats
 * @version 1.2.0
 */
public class DashboardStatsDTO {
    private Integer globalScore;
    private Double courseAverageScore;
    private Map<String, Integer> teamVelocityHistory;
    private List<AchievementResponseDTO> recentBadges;

    // Story tracking fields
    private Integer storiesCompleted;
    private Integer totalStoryPoints;
    private Integer totalBadges;
    // (Badge + Date). Prompt said "List<BadgeResponseDTO>
    // recentBadges". Using AchievementResponseDTO might be better or
    // I should use BadgeResponseDTO as requested.
    // Prompt said: "List<BadgeResponseDTO> recentBadges".
    // I will check if BadgeResponseDTO exists. If not, I'll use
    // AchievementResponseDTO or create one.
    // Actually, dashboard usually shows *earned* badges, which are Achievements.
    // But prompt asked for BadgeResponseDTO.
    // If I return BadgeResponseDTO, I lose the "when" info.
    // I'll stick to prompt "BadgeResponseDTO" but I suspect it means "Recent Earned
    // Badges".
    // Let's check if BadgeResponseDTO exists in the list_dir output.

    /** Default constructor. */
    public DashboardStatsDTO() {
    }

    public DashboardStatsDTO(Integer globalScore, Double courseAverageScore, Map<String, Integer> teamVelocityHistory,
            List<AchievementResponseDTO> recentBadges, Integer storiesCompleted, Integer totalStoryPoints,
            Integer totalBadges) {
        this.globalScore = globalScore;
        this.courseAverageScore = courseAverageScore;
        this.teamVelocityHistory = teamVelocityHistory;
        this.recentBadges = recentBadges;
        this.storiesCompleted = storiesCompleted;
        this.totalStoryPoints = totalStoryPoints;
        this.totalBadges = totalBadges;
    }

    /** @return The global score */
    public Integer getGlobalScore() {
        return globalScore;
    }

    public void setGlobalScore(Integer globalScore) {
        this.globalScore = globalScore;
    }

    public Double getCourseAverageScore() {
        return courseAverageScore;
    }

    public void setCourseAverageScore(Double courseAverageScore) {
        this.courseAverageScore = courseAverageScore;
    }

    public Map<String, Integer> getTeamVelocityHistory() {
        return teamVelocityHistory;
    }

    public void setTeamVelocityHistory(Map<String, Integer> teamVelocityHistory) {
        this.teamVelocityHistory = teamVelocityHistory;
    }

    public List<AchievementResponseDTO> getRecentBadges() {
        return recentBadges;
    }

    public void setRecentBadges(List<AchievementResponseDTO> recentBadges) {
        this.recentBadges = recentBadges;
    }

    public Integer getStoriesCompleted() {
        return storiesCompleted;
    }

    public void setStoriesCompleted(Integer storiesCompleted) {
        this.storiesCompleted = storiesCompleted;
    }

    public Integer getTotalStoryPoints() {
        return totalStoryPoints;
    }

    public void setTotalStoryPoints(Integer totalStoryPoints) {
        this.totalStoryPoints = totalStoryPoints;
    }

    public Integer getTotalBadges() {
        return totalBadges;
    }

    public void setTotalBadges(Integer totalBadges) {
        this.totalBadges = totalBadges;
    }
}
