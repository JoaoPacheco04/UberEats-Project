package com.eduscrum.upt.Ubereats.repository;

import com.eduscrum.upt.Ubereats.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Team entity.
 * Provides CRUD operations and team-specific queries.
 *
 * @author UberEats
 * @version 0.5.0
 */
@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    // Find team assigned to a specific project (single team per project)
    @Query("SELECT t FROM Team t JOIN t.projects p WHERE p.id = :projectId")
    Optional<Team> findByProjectId(@Param("projectId") Long projectId);

    /**
     * Checks if a team with the given name exists.
     *
     * @param name The team name
     * @return true if name exists
     */
    boolean existsByName(String name);

    /**
     * Finds teams where a user is an active member.
     *
     * @param userId The user ID
     * @return List of teams the user belongs to
     */
    @Query("SELECT t FROM Team t JOIN t.members m WHERE m.user.id = :userId AND m.isActive = true")
    List<Team> findTeamsByUserId(@Param("userId") Long userId);

    /**
     * Counts completed projects for a team in a course.
     *
     * @param teamId   The team ID
     * @param courseId The course ID
     * @return Count of completed projects
     */
    @Query("SELECT COUNT(p) FROM Team t JOIN t.projects p " +
            "WHERE t.id = :teamId " +
            "AND p.course.id = :courseId " +
            "AND p.status = com.eduscrum.upt.Ubereats.entity.enums.ProjectStatus.COMPLETED")
    Long countCompletedProjectsByTeamInCourse(@Param("teamId") Long teamId, @Param("courseId") Long courseId);

}
