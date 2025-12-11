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
 * @author UberEats
 * @version 0.9.1
 */
@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {

<<<<<<< HEAD
        /**
         * Finds a badge by its name.
         *
         * @param name The badge name
         * @return Optional containing the badge if found
         */
        Optional<Badge> findByName(String name);

        /**
         * Finds badges by type.
         *
         * @param badgeType The badge type
         * @return List of badges with the given type
         */
        List<Badge> findByBadgeType(BadgeType badgeType);

        /**
         * Finds all active badges.
         *
         * @return List of active badges
         */
        List<Badge> findByIsActiveTrue();

        /**
         * Finds badges by creator ID.
         *
         * @param createdById The creator user ID
         * @return List of badges created by the user
         */
        List<Badge> findByCreatedById(Long createdById);

        /**
         * Finds active badges of a specific type.
         *
         * @param badgeType The badge type
         * @return List of active badges with the given type
         */
        List<Badge> findByBadgeTypeAndIsActiveTrue(BadgeType badgeType);

        /**
         * Checks if a badge with the given name exists.
         *
         * @param name The badge name
         * @return true if name exists
         */
        boolean existsByName(String name);

        /**
         * Checks if a badge name exists, excluding a specific badge.
         *
         * @param name The badge name
         * @param id   The badge ID to exclude
         * @return true if name exists in other badges
         */
        boolean existsByNameAndIdNot(String name, Long id);

        /**
         * Finds a badge by ID with its achievements.
         *
         * @param id The badge ID
         * @return Optional containing the badge with achievements
         */
        @Query("SELECT b FROM Badge b LEFT JOIN FETCH b.achievements WHERE b.id = :id")
        Optional<Badge> findByIdWithAchievements(@Param("id") Long id);

        /**
         * Finds most awarded badges.
         *
         * @return List of [Badge, awardCount] arrays
         */
        @Query("SELECT b, COUNT(a) as awardCount FROM Badge b LEFT JOIN b.achievements a GROUP BY b ORDER BY awardCount DESC")
        List<Object[]> findMostAwardedBadges();

        /**
         * Sums completed story points for a user in a project.
         *
         * @param userId    The user ID
         * @param projectId The project ID
         * @return Total completed story points
         */
=======
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

>>>>>>> Yesh_Branch
        @Query("SELECT COALESCE(SUM(us.storyPoints), 0) FROM UserStory us " +
                        "WHERE us.assignedTo.id = :userId " +
                        "AND us.sprint.project.id = :projectId " +
                        "AND us.status = 'DONE'")
        Integer sumCompletedStoryPointsByProject(
                        @Param("userId") Long userId,
                        @Param("projectId") Long projectId);
<<<<<<< HEAD
}
=======
}
>>>>>>> Yesh_Branch
