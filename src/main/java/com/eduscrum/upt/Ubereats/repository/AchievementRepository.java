// AchievementRepository.java
package com.eduscrum.upt.Ubereats.repository;

import com.eduscrum.upt.Ubereats.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Achievement entity.
 * Provides CRUD operations and achievement-specific queries.
 *
 * @author Yeswanth Kumar
 * @author Joao Pacheco
 * @version 0.6.1
 */
@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    /**
     * Finds achievements by user ID.
     *
     * @param userId The user ID
     * @return List of achievements for the user
     */
    List<Achievement> findByAwardedToUserId(Long userId);

    /**
     * Finds achievements by team ID.
     *
     * @param teamId The team ID
     * @return List of achievements for the team
     */
    List<Achievement> findByAwardedToTeamId(Long teamId);

    /**
     * Finds achievements by project ID.
     *
     * @param projectId The project ID
     * @return List of achievements in the project
     */
    List<Achievement> findByProjectId(Long projectId);

    /**
     * Finds achievements by badge ID.
     *
     * @param badgeId The badge ID
     * @return List of achievements for the badge
     */
    List<Achievement> findByBadgeId(Long badgeId);

    /**
     * Finds achievements by sprint ID.
     *
     * @param sprintId The sprint ID
     * @return List of achievements in the sprint
     */
    List<Achievement> findBySprintId(Long sprintId);

    /**
     * Finds achievements awarded by a specific user.
     *
     * @param awardedById The awarding user ID
     * @return List of achievements awarded by the user
     */
    List<Achievement> findByAwardedById(Long awardedById);

    /**
     * Checks if user has specific badge in project.
     *
     * @param userId    The user ID
     * @param badgeId   The badge ID
     * @param projectId The project ID
     * @return true if user has the badge in the project
     */
    @Query("SELECT COUNT(a) > 0 FROM Achievement a WHERE a.awardedToUser.id = :userId AND a.badge.id = :badgeId AND a.project.id = :projectId")
    boolean existsByUserIdAndBadgeIdAndProjectId(@Param("userId") Long userId, @Param("badgeId") Long badgeId,
            @Param("projectId") Long projectId);

    /**
     * Checks if team has specific badge.
     *
     * @param teamId  The team ID
     * @param badgeId The badge ID
     * @return true if team has the badge
     */
    @Query("SELECT COUNT(a) > 0 FROM Achievement a WHERE a.awardedToTeam.id = :teamId AND a.badge.id = :badgeId")
    boolean existsByTeamIdAndBadgeId(@Param("teamId") Long teamId, @Param("badgeId") Long badgeId);

    /**
     * Gets user's total points in project.
     *
     * @param userId    The user ID
     * @param projectId The project ID
     * @return Total points earned
     */
    @Query("SELECT COALESCE(SUM(a.badge.points), 0) FROM Achievement a WHERE a.awardedToUser.id = :userId AND a.project.id = :projectId")
    Integer sumUserPointsInProject(@Param("userId") Long userId, @Param("projectId") Long projectId);

    /**
     * Gets team's total points.
     *
     * @param teamId The team ID
     * @return Total points earned by the team
     */
    @Query("SELECT COALESCE(SUM(a.badge.points), 0) FROM Achievement a WHERE a.awardedToTeam.id = :teamId")
    Integer sumTeamPoints(@Param("teamId") Long teamId);

    /**
     * Finds latest achievements.
     *
     * @return List of achievements ordered by date
     */
    @Query("SELECT a FROM Achievement a ORDER BY a.awardedAt DESC")
    List<Achievement> findLatestAchievements();
}
