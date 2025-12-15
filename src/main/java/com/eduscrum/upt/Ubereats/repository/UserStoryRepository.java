package com.eduscrum.upt.Ubereats.repository;

import com.eduscrum.upt.Ubereats.entity.UserStory;
import com.eduscrum.upt.Ubereats.entity.enums.StoryPriority;
import com.eduscrum.upt.Ubereats.entity.enums.StoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for UserStory entity.
 * Provides CRUD operations and user story queries.
 *
 * @author Francisco
 * @author Ana
 * @version 0.8.0
 */
@Repository
public interface UserStoryRepository extends JpaRepository<UserStory, Long> {

        /**
         * Finds user stories by sprint ID.
         *
         * @param sprintId The sprint ID
         * @return List of user stories in the sprint
         */
        List<UserStory> findBySprintId(Long sprintId);

        /**
         * Finds user stories by team ID.
         *
         * @param teamId The team ID
         * @return List of user stories for the team
         */
        List<UserStory> findByTeamId(Long teamId);

        /**
         * Finds user stories by assigned user ID.
         *
         * @param assignedToId The assigned user ID
         * @return List of user stories assigned to the user
         */
        List<UserStory> findByAssignedToId(Long assignedToId);

        /**
         * Finds user stories by creator ID.
         *
         * @param createdById The creator user ID
         * @return List of user stories created by the user
         */
        List<UserStory> findByCreatedById(Long createdById);

        /**
         * Finds user stories by status.
         *
         * @param status The story status
         * @return List of user stories with the given status
         */
        List<UserStory> findByStatus(StoryStatus status);

        /**
         * Finds user stories by priority.
         *
         * @param priority The story priority
         * @return List of user stories with the given priority
         */
        List<UserStory> findByPriority(StoryPriority priority);

        /**
         * Finds user stories by sprint ID and status.
         *
         * @param sprintId The sprint ID
         * @param status   The story status
         * @return List of matching user stories
         */
        List<UserStory> findBySprintIdAndStatus(Long sprintId, StoryStatus status);

        /**
         * Finds user stories by team ID and status.
         *
         * @param teamId The team ID
         * @param status The story status
         * @return List of matching user stories
         */
        List<UserStory> findByTeamIdAndStatus(Long teamId, StoryStatus status);

        /**
         * Finds completed user stories in a sprint.
         *
         * @param sprintId The sprint ID
         * @return List of completed user stories
         */
        @Query("SELECT us FROM UserStory us WHERE us.sprint.id = :sprintId AND us.status = com.eduscrum.upt.Ubereats.entity.enums.StoryStatus.DONE")
        List<UserStory> findCompletedStoriesBySprint(@Param("sprintId") Long sprintId);

        /**
         * Calculates total story points in a sprint.
         *
         * @param sprintId The sprint ID
         * @return Total story points in the sprint
         */
        @Query("SELECT COALESCE(SUM(us.storyPoints), 0) FROM UserStory us WHERE us.sprint.id = :sprintId")
        Integer sumStoryPointsBySprint(@Param("sprintId") Long sprintId);

        /**
         * Calculates completed story points in a sprint.
         *
         * @param sprintId The sprint ID
         * @return Completed story points in the sprint
         */
        @Query("SELECT COALESCE(SUM(us.storyPoints), 0) FROM UserStory us WHERE us.sprint.id = :sprintId AND us.status = com.eduscrum.upt.Ubereats.entity.enums.StoryStatus.DONE")
        Integer sumCompletedStoryPointsBySprint(@Param("sprintId") Long sprintId);

        /**
         * Finds user stories by multiple criteria.
         *
         * @param sprintId     The sprint ID (optional)
         * @param teamId       The team ID (optional)
         * @param status       The story status (optional)
         * @param assignedToId The assigned user ID (optional)
         * @return List of matching user stories
         */
        @Query("SELECT us FROM UserStory us WHERE " +
                        "(:sprintId IS NULL OR us.sprint.id = :sprintId) AND " +
                        "(:teamId IS NULL OR us.team.id = :teamId) AND " +
                        "(:status IS NULL OR us.status = :status) AND " +
                        "(:assignedToId IS NULL OR us.assignedTo.id = :assignedToId)")
        List<UserStory> findByCriteria(@Param("sprintId") Long sprintId,
                        @Param("teamId") Long teamId,
                        @Param("status") StoryStatus status,
                        @Param("assignedToId") Long assignedToId);

        /**
         * Checks if a title exists in a sprint.
         *
         * @param sprintId The sprint ID
         * @param title    The title to check
         * @return true if title exists
         */
        boolean existsBySprintIdAndTitle(Long sprintId, String title);

        /**
         * Checks if a title exists in a sprint, excluding a specific story.
         *
         * @param sprintId The sprint ID
         * @param title    The title to check
         * @param id       The story ID to exclude
         * @return true if title exists in other stories
         */
        boolean existsBySprintIdAndTitleAndIdNot(Long sprintId, String title, Long id);

        /**
         * Sums high-priority completed story points for a user in a project.
         *
         * @param userId    The user ID
         * @param projectId The project ID
         * @return Total high-priority story points completed
         */
        @Query("SELECT COALESCE(SUM(us.storyPoints), 0) FROM UserStory us " +
                        "WHERE us.assignedTo.id = :userId " +
                        "AND us.sprint.project.id = :projectId " +
                        "AND us.status = com.eduscrum.upt.Ubereats.entity.enums.StoryStatus.DONE " +
                        "AND us.priority IN (com.eduscrum.upt.Ubereats.entity.enums.StoryPriority.HIGH, com.eduscrum.upt.Ubereats.entity.enums.StoryPriority.CRITICAL)")
        Integer sumHighPriorityCompletedStoryPointsByProject(
                        @Param("userId") Long userId,
                        @Param("projectId") Long projectId);

        /**
         * Sums completed story points per sprint for a user in a project.
         *
         * @param userId    The user ID
         * @param projectId The project ID
         * @return List of [sprintId, completedPoints] arrays
         */
        @Query("SELECT us.sprint.id, SUM(us.storyPoints) FROM UserStory us " +
                        "WHERE us.assignedTo.id = :userId " +
                        "AND us.sprint.project.id = :projectId " +
                        "AND us.status = com.eduscrum.upt.Ubereats.entity.enums.StoryStatus.DONE " +
                        "GROUP BY us.sprint.id")
        List<Object[]> sumCompletedStoryPointsPerSprintInProject(
                        @Param("userId") Long userId,
                        @Param("projectId") Long projectId);

        /**
         * Finds completed story points by team per completed sprint.
         *
         * @param teamId The team ID
         * @return List of [sprintId, completedPoints] arrays
         */
        @Query("SELECT us.sprint.id, SUM(us.storyPoints) FROM UserStory us " +
                        "WHERE us.team.id = :teamId " +
                        "AND us.sprint.status = com.eduscrum.upt.Ubereats.entity.enums.SprintStatus.COMPLETED " +
                        "AND us.status = com.eduscrum.upt.Ubereats.entity.enums.StoryStatus.DONE " +
                        "GROUP BY us.sprint.id")
        List<Object[]> findCompletedPointsByTeamAndCompletedSprints(@Param("teamId") Long teamId);

        // === Analytics Auto-Trigger Queries ===

        /**
         * Counts total user stories for a sprint and team.
         *
         * @param sprintId The sprint ID
         * @param teamId   The team ID
         * @return Count of user stories
         */
        @Query("SELECT COUNT(us) FROM UserStory us WHERE us.sprint.id = :sprintId AND us.team.id = :teamId")
        Integer countBySprintIdAndTeamId(@Param("sprintId") Long sprintId, @Param("teamId") Long teamId);

        /**
         * Counts completed user stories for a sprint and team.
         *
         * @param sprintId The sprint ID
         * @param teamId   The team ID
         * @return Count of completed user stories
         */
        @Query("SELECT COUNT(us) FROM UserStory us WHERE us.sprint.id = :sprintId AND us.team.id = :teamId AND us.status = com.eduscrum.upt.Ubereats.entity.enums.StoryStatus.DONE")
        Integer countCompletedBySprintIdAndTeamId(@Param("sprintId") Long sprintId, @Param("teamId") Long teamId);

        /**
         * Sums total story points for a sprint and team.
         *
         * @param sprintId The sprint ID
         * @param teamId   The team ID
         * @return Total story points
         */
        @Query("SELECT COALESCE(SUM(us.storyPoints), 0) FROM UserStory us WHERE us.sprint.id = :sprintId AND us.team.id = :teamId")
        Integer sumStoryPointsBySprintIdAndTeamId(@Param("sprintId") Long sprintId, @Param("teamId") Long teamId);

        /**
         * Sums completed story points for a sprint and team.
         *
         * @param sprintId The sprint ID
         * @param teamId   The team ID
         * @return Completed story points
         */
        @Query("SELECT COALESCE(SUM(us.storyPoints), 0) FROM UserStory us WHERE us.sprint.id = :sprintId AND us.team.id = :teamId AND us.status = com.eduscrum.upt.Ubereats.entity.enums.StoryStatus.DONE")
        Integer sumCompletedStoryPointsBySprintIdAndTeamId(@Param("sprintId") Long sprintId,
                        @Param("teamId") Long teamId);
}
