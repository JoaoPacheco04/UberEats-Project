// SprintRepository.java
package com.eduscrum.upt.Ubereats.repository;

import com.eduscrum.upt.Ubereats.entity.Sprint;
import com.eduscrum.upt.Ubereats.entity.enums.SprintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Sprint entity.
 * Provides CRUD operations and sprint-specific queries.
 *
 * @version 0.1.0 (2025-10-15)
 */
@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {

    // Find sprints by project
    List<Sprint> findByProjectId(Long projectId);

    // Find sprints by status
    List<Sprint> findByStatus(SprintStatus status);

    // Find sprints by project and status
    List<Sprint> findByProjectIdAndStatus(Long projectId, SprintStatus status);

    // Find active sprints (in progress)
    @Query("SELECT s FROM Sprint s WHERE s.status = 'IN_PROGRESS'")
    List<Sprint> findActiveSprints();

    // Find sprints by date range
    List<Sprint> findByStartDateBetweenOrEndDateBetween(LocalDate start1, LocalDate end1, LocalDate start2,
            LocalDate end2);

    // Find overdue sprints
    @Query("SELECT s FROM Sprint s WHERE s.endDate < :today AND s.status != 'COMPLETED'")
    List<Sprint> findOverdueSprints(@Param("today") LocalDate today);

    // Find latest sprint for a project
    @Query("SELECT s FROM Sprint s WHERE s.project.id = :projectId ORDER BY s.sprintNumber DESC LIMIT 1")
    Optional<Sprint> findLatestSprintByProject(@Param("projectId") Long projectId);

    // Check if sprint number exists in project
    boolean existsByProjectIdAndSprintNumber(Long projectId, Integer sprintNumber);

    // Check if sprint number exists in project excluding current sprint
    boolean existsByProjectIdAndSprintNumberAndIdNot(Long projectId, Integer sprintNumber, Long id);

    // Find sprints that can be started (planned and start date reached)
    @Query("SELECT s FROM Sprint s WHERE s.status = 'PLANNED' AND s.startDate <= :today")
    List<Sprint> findSprintsReadyToStart(@Param("today") LocalDate today);

    // Find sprints that can be completed (in progress and end date reached)
    @Query("SELECT s FROM Sprint s WHERE s.status = 'IN_PROGRESS' AND s.endDate <= :today")
    List<Sprint> findSprintsReadyToComplete(@Param("today") LocalDate today);
}
