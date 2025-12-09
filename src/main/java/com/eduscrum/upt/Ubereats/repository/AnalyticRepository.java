package com.eduscrum.upt.Ubereats.repository;

import com.eduscrum.upt.Ubereats.entity.Analytic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyticRepository extends JpaRepository<Analytic, Long> {

    @Query("SELECT a FROM Analytic a WHERE a.team.id = :teamId AND a.sprint.id = :sprintId " +
            "ORDER BY a.recordedDate DESC, a.createdAt DESC")
    List<Analytic> findLatestByTeamAndSprint(@Param("teamId") Long teamId, @Param("sprintId") Long sprintId);

    Optional<Analytic> findByTeamIdAndSprintIdAndRecordedDate(Long teamId, Long sprintId, LocalDate recordedDate);

    List<Analytic> findByTeamIdAndSprintProjectId(@Param("teamId") Long teamId, @Param("projectId") Long projectId);
}