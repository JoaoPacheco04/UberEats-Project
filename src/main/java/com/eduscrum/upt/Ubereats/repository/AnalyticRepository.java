package com.eduscrum.upt.Ubereats.repository;

import com.eduscrum.upt.Ubereats.entity.Analytic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Analytic entity.
 * Provides CRUD operations and analytics queries.
 *
 * @author UberEats
 * @version 0.8.0
 */
@Repository
public interface AnalyticRepository extends JpaRepository<Analytic, Long> {

    /**
     * Finds latest analytics by team and sprint.
     *
     * @param teamId   The team ID
     * @param sprintId The sprint ID
     * @return List of analytics ordered by date
     */
    @Query("SELECT a FROM Analytic a WHERE a.team.id = :teamId AND a.sprint.id = :sprintId " +
            "ORDER BY a.recordedDate DESC, a.createdAt DESC")
    List<Analytic> findLatestByTeamAndSprint(@Param("teamId") Long teamId, @Param("sprintId") Long sprintId);

    /**
     * Finds analytic by team, sprint, and date.
     *
     * @param teamId       The team ID
     * @param sprintId     The sprint ID
     * @param recordedDate The recorded date
     * @return Optional containing the analytic
     */
    Optional<Analytic> findByTeamIdAndSprintIdAndRecordedDate(Long teamId, Long sprintId, LocalDate recordedDate);

    /**
     * Finds analytics by team and project.
     *
     * @param teamId    The team ID
     * @param projectId The project ID
     * @return List of analytics
     */
    List<Analytic> findByTeamIdAndSprintProjectId(@Param("teamId") Long teamId, @Param("projectId") Long projectId);

    /**
     * Finds all analytics for a project (all sprints, all teams).
     *
     * @param projectId The project ID
     * @return List of analytics for the project
     */
    @Query("SELECT a FROM Analytic a WHERE a.sprint.project.id = :projectId ORDER BY a.sprint.id, a.recordedDate ASC")
    List<Analytic> findByProjectId(@Param("projectId") Long projectId);
}
