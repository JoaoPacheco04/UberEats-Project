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

    // Find teams by project ID
    List<Team> findByProjects_Id(Long projectId);

    // Check if a team with a given name exists
    boolean existsByName(String name);

    // Find teams where a user is an active member
    @Query("SELECT t FROM Team t JOIN t.members m WHERE m.user.id = :userId AND m.isActive = true")
    List<Team> findTeamsByUserId(@Param("userId") Long userId);

    // Count the number of completed projects for a team within a specific course
    @Query("SELECT COUNT(p) FROM Team t JOIN t.projects p " +
           "WHERE t.id = :teamId " +
           "AND p.course.id = :courseId " +
           "AND p.status = com.eduscrum.upt.Ubereats.entity.enums.ProjectStatus.COMPLETED")
    Long countCompletedProjectsByTeamInCourse(@Param("teamId") Long teamId, @Param("courseId") Long courseId);

}