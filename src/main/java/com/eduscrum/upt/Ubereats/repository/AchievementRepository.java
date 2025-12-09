// AchievementRepository.java
package com.eduscrum.upt.Ubereats.repository;

import com.eduscrum.upt.Ubereats.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    // Find achievements by user
    List<Achievement> findByAwardedToUserId(Long userId);

    // Find achievements by team
    List<Achievement> findByAwardedToTeamId(Long teamId);

    // Find achievements by project
    List<Achievement> findByProjectId(Long projectId);

    // Find achievements by badge
    List<Achievement> findByBadgeId(Long badgeId);

    // Find achievements by sprint
    List<Achievement> findBySprintId(Long sprintId);

    // Find achievements awarded by specific user
    List<Achievement> findByAwardedById(Long awardedById);

    // Check if user has specific badge in project
    @Query("SELECT COUNT(a) > 0 FROM Achievement a WHERE a.awardedToUser.id = :userId AND a.badge.id = :badgeId AND a.project.id = :projectId")
    boolean existsByUserIdAndBadgeIdAndProjectId(@Param("userId") Long userId, @Param("badgeId") Long badgeId,
            @Param("projectId") Long projectId);

    // Check if team has specific badge
    @Query("SELECT COUNT(a) > 0 FROM Achievement a WHERE a.awardedToTeam.id = :teamId AND a.badge.id = :badgeId")
    boolean existsByTeamIdAndBadgeId(@Param("teamId") Long teamId, @Param("badgeId") Long badgeId);

    // Get user's total points in project
    @Query("SELECT COALESCE(SUM(a.badge.points), 0) FROM Achievement a WHERE a.awardedToUser.id = :userId AND a.project.id = :projectId")
    Integer sumUserPointsInProject(@Param("userId") Long userId, @Param("projectId") Long projectId);

    // Get team's total points
    @Query("SELECT COALESCE(SUM(a.badge.points), 0) FROM Achievement a WHERE a.awardedToTeam.id = :teamId")
    Integer sumTeamPoints(@Param("teamId") Long teamId);

    // Find latest achievements with pagination
    @Query("SELECT a FROM Achievement a ORDER BY a.awardedAt DESC")
    List<Achievement> findLatestAchievements();
}