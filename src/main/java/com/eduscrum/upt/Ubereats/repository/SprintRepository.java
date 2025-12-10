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
 * @author UberEats
 * @version 0.1.0
 */
@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {

    /**
     * Finds sprints by project ID.
     *
     * @param projectId The project ID
     * @return List of sprints in the project
     */
    List<Sprint> findByProjectId(Long projectId);

    /**
     * Finds sprints by status.
     *
     * @param status The sprint status
     * @return List of sprints with the given status
     */
    List<Sprint> findByStatus(SprintStatus status);

    /**
     * Finds sprints by project ID and status.
     *
     * @param projectId The project ID
     * @param status    The sprint status
     * @return List of matching sprints
     */
    List<Sprint> findByProjectIdAndStatus(Long projectId, SprintStatus status);

    /**
     * Finds active sprints (in progress).
     *
     * @return List of active sprints
     */
    @Query("SELECT s FROM Sprint s WHERE s.status = 'IN_PROGRESS'")
    List<Sprint> findActiveSprints();

    /**
     * Finds sprints by date range.
     *
     * @param start1 Start date range start
     * @param end1   Start date range end
     * @param start2 End date range start
     * @param end2   End date range end
     * @return List of sprints in the date ranges
     */
    List<Sprint> findByStartDateBetweenOrEndDateBetween(LocalDate start1, LocalDate end1, LocalDate start2,
            LocalDate end2);

    /**
     * Finds overdue sprints.
     *
     * @param today Current date
     * @return List of overdue sprints
     */
    @Query("SELECT s FROM Sprint s WHERE s.endDate < :today AND s.status != 'COMPLETED'")
    List<Sprint> findOverdueSprints(@Param("today") LocalDate today);

    /**
     * Finds latest sprint for a project.
     *
     * @param projectId The project ID
     * @return Optional containing the latest sprint
     */
    @Query("SELECT s FROM Sprint s WHERE s.project.id = :projectId ORDER BY s.sprintNumber DESC LIMIT 1")
    Optional<Sprint> findLatestSprintByProject(@Param("projectId") Long projectId);

    /**
     * Checks if sprint number exists in project.
     *
     * @param projectId    The project ID
     * @param sprintNumber The sprint number
     * @return true if sprint number exists
     */
    boolean existsByProjectIdAndSprintNumber(Long projectId, Integer sprintNumber);

    /**
     * Checks if sprint number exists in project, excluding a specific sprint.
     *
     * @param projectId    The project ID
     * @param sprintNumber The sprint number
     * @param id           The sprint ID to exclude
     * @return true if sprint number exists in other sprints
     */
    boolean existsByProjectIdAndSprintNumberAndIdNot(Long projectId, Integer sprintNumber, Long id);

    /**
     * Finds sprints ready to start.
     *
     * @param today Current date
     * @return List of sprints ready to start
     */
    @Query("SELECT s FROM Sprint s WHERE s.status = 'PLANNED' AND s.startDate <= :today")
    List<Sprint> findSprintsReadyToStart(@Param("today") LocalDate today);

    /**
     * Finds sprints ready to complete.
     *
     * @param today Current date
     * @return List of sprints ready to complete
     */
    @Query("SELECT s FROM Sprint s WHERE s.status = 'IN_PROGRESS' AND s.endDate <= :today")
    List<Sprint> findSprintsReadyToComplete(@Param("today") LocalDate today);
}
