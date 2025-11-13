// UserStoryRepository.java
package com.eduscrum.upt.Ubereats.repository;

import com.eduscrum.upt.Ubereats.entity.UserStory;
import com.eduscrum.upt.Ubereats.entity.enums.StoryPriority;
import com.eduscrum.upt.Ubereats.entity.enums.StoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStoryRepository extends JpaRepository<UserStory, Long> {

    // Find user stories by sprint
    List<UserStory> findBySprintId(Long sprintId);

    // Find user stories by team
    List<UserStory> findByTeamId(Long teamId);

    // Find user stories by assigned user
    List<UserStory> findByAssignedToId(Long assignedToId);

    // Find user stories by creator
    List<UserStory> findByCreatedById(Long createdById);

    // Find user stories by status
    List<UserStory> findByStatus(StoryStatus status);

    // Find user stories by priority
    List<UserStory> findByPriority(StoryPriority priority);

    // Find user stories by sprint and status
    List<UserStory> findBySprintIdAndStatus(Long sprintId, StoryStatus status);

    // Find user stories by team and status
    List<UserStory> findByTeamIdAndStatus(Long teamId, StoryStatus status);

    // Find completed user stories in sprint
    @Query("SELECT us FROM UserStory us WHERE us.sprint.id = :sprintId AND us.status = 'DONE'")
    List<UserStory> findCompletedStoriesBySprint(@Param("sprintId") Long sprintId);

    // Calculate total story points in sprint
    @Query("SELECT COALESCE(SUM(us.storyPoints), 0) FROM UserStory us WHERE us.sprint.id = :sprintId")
    Integer sumStoryPointsBySprint(@Param("sprintId") Long sprintId);

    // Calculate completed story points in sprint
    @Query("SELECT COALESCE(SUM(us.storyPoints), 0) FROM UserStory us WHERE us.sprint.id = :sprintId AND us.status = 'DONE'")
    Integer sumCompletedStoryPointsBySprint(@Param("sprintId") Long sprintId);

    // Find user stories by multiple criteria
    @Query("SELECT us FROM UserStory us WHERE " +
            "(:sprintId IS NULL OR us.sprint.id = :sprintId) AND " +
            "(:teamId IS NULL OR us.team.id = :teamId) AND " +
            "(:status IS NULL OR us.status = :status) AND " +
            "(:assignedToId IS NULL OR us.assignedTo.id = :assignedToId)")
    List<UserStory> findByCriteria(@Param("sprintId") Long sprintId,
                                   @Param("teamId") Long teamId,
                                   @Param("status") StoryStatus status,
                                   @Param("assignedToId") Long assignedToId);

    // Check if title exists in sprint
    boolean existsBySprintIdAndTitle(Long sprintId, String title);

    // Check if title exists in sprint excluding current story
    boolean existsBySprintIdAndTitleAndIdNot(Long sprintId, String title, Long id);
}