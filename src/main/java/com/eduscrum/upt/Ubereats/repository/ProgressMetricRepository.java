package com.eduscrum.upt.Ubereats.repository;

import com.eduscrum.upt.Ubereats.entity.ProgressMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressMetricRepository extends JpaRepository<ProgressMetric, Long> {

    @Query("SELECT pm FROM ProgressMetric pm WHERE pm.team.id = :teamId AND pm.sprint.id = :sprintId " +
            "ORDER BY pm.recordedDate DESC, pm.createdAt DESC")
    List<ProgressMetric> findLatestByTeamAndSprint(@Param("teamId") Long teamId, @Param("sprintId") Long sprintId);

    Optional<ProgressMetric> findByTeamIdAndSprintIdAndRecordedDate(Long teamId, Long sprintId, LocalDate recordedDate);

    List<ProgressMetric> findByTeamIdAndSprintProjectId(@Param("teamId") Long teamId, @Param("projectId") Long projectId);
}