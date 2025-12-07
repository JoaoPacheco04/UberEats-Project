package com.eduscrum.upt.Ubereats.repository;

import com.eduscrum.upt.Ubereats.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    // Find teams by project
    List<Team> findByProjectId(Long projectId);

    // Find teams by course (through project)
    @Query("SELECT t FROM Team t WHERE t.project.course.id = :courseId")
    List<Team> findByCourseId(@Param("courseId") Long courseId);

    // Check if team name already exists in project
    boolean existsByNameAndProjectId(String name, Long projectId);

    // Find teams where user is a member
    @Query("SELECT t FROM Team t JOIN t.members m WHERE m.user.id = :userId AND m.isActive = true")
    List<Team> findTeamsByUserId(@Param("userId") Long userId);

    // Count active teams in project
    @Query("SELECT COUNT(t) FROM Team t WHERE t.project.id = :projectId")
    Long countByProjectId(@Param("projectId") Long projectId);

    // Find team by name and project
    Optional<Team> findByNameAndProjectId(String name, Long projectId);

    @Query("SELECT COUNT(t.project) FROM Team t " +
            "WHERE t.id = :teamId " +
            "AND t.project.course.id = :courseId " +
            "AND t.project.status = 'COMPLETED'")
    Long countCompletedProjectsByTeamInCourse(
            @Param("teamId") Long teamId,
            @Param("courseId") Long courseId);
}