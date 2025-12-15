// BadgeRepository.java
package com.eduscrum.upt.Ubereats.repository;

import com.eduscrum.upt.Ubereats.entity.Badge;
import com.eduscrum.upt.Ubereats.entity.enums.BadgeType;
import com.eduscrum.upt.Ubereats.entity.enums.RecipientType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Badge entity.
 * Provides CRUD operations and badge-specific queries.
 *
 * @author Ana
 * @author Bruna
 * @version 0.9.1
 */
@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {

        // Find by name
        Optional<Badge> findByName(String name);

        // Find by badge type
        List<Badge> findByBadgeType(BadgeType badgeType);

        // Find by recipient type
        List<Badge> findByRecipientType(RecipientType recipientType);

        // Find active badges by recipient type
        List<Badge> findByIsActiveTrueAndRecipientTypeIn(List<RecipientType> recipientTypes);

        // Find active badges
        List<Badge> findByIsActiveTrue();

        // Find by creator
        List<Badge> findByCreatedById(Long createdById);

        // Find automatic badges
        List<Badge> findByBadgeTypeAndIsActiveTrue(BadgeType badgeType);

        // Check if name exists (for creation/update validation)
        boolean existsByName(String name);

        // Check if name exists excluding current badge (for update validation)
        boolean existsByNameAndIdNot(String name, Long id);

        // Find badges with award count
        @Query("SELECT b FROM Badge b LEFT JOIN FETCH b.achievements WHERE b.id = :id")
        Optional<Badge> findByIdWithAchievements(@Param("id") Long id);

        // Find most awarded badges
        @Query("SELECT b, COUNT(a) as awardCount FROM Badge b LEFT JOIN b.achievements a GROUP BY b ORDER BY awardCount DESC")
        List<Object[]> findMostAwardedBadges();

        @Query("SELECT COALESCE(SUM(us.storyPoints), 0) FROM UserStory us " +
                        "WHERE us.assignedTo.id = :userId " +
                        "AND us.sprint.project.id = :projectId " +
                        "AND us.status = 'DONE'")
        Integer sumCompletedStoryPointsByProject(
                        @Param("userId") Long userId,
                        @Param("projectId") Long projectId);
}
