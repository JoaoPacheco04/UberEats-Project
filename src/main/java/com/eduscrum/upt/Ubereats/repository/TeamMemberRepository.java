package com.eduscrum.upt.Ubereats.repository;

import com.eduscrum.upt.Ubereats.entity.TeamMember;
import com.eduscrum.upt.Ubereats.entity.enums.ScrumRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TeamMember entity.
 * Provides CRUD operations and team membership queries.
 *
 * @version 0.2.1 (2025-10-22)
 */
@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    /**
     * Finds active team members by team ID.
     *
     * @param teamId The team ID
     * @return List of active team members
     */
    List<TeamMember> findByTeamIdAndIsActiveTrue(Long teamId);

    /**
     * Finds a team member by user ID and team ID.
     *
     * @param userId The user ID
     * @param teamId The team ID
     * @return Optional containing the team member
     */
    Optional<TeamMember> findByUserIdAndTeamId(Long userId, Long teamId);

    /**
     * Finds all teams where user is an active member.
     *
     * @param userId The user ID
     * @return List of team memberships
     */
    List<TeamMember> findByUserIdAndIsActiveTrue(Long userId);

    /**
     * Counts active members in a team.
     *
     * @param teamId The team ID
     * @return Count of active members
     */
    Long countByTeamIdAndIsActiveTrue(Long teamId);

    /**
     * Finds the Scrum Master of a team.
     *
     * @param teamId The team ID
     * @return Optional containing the Scrum Master
     */
    @Query("SELECT tm FROM TeamMember tm WHERE tm.team.id = :teamId AND tm.role = 'SCRUM_MASTER' AND tm.isActive = true")
    Optional<TeamMember> findScrumMasterByTeamId(@Param("teamId") Long teamId);

    /**
     * Finds the Product Owner of a team.
     *
     * @param teamId The team ID
     * @return Optional containing the Product Owner
     */
    @Query("SELECT tm FROM TeamMember tm WHERE tm.team.id = :teamId AND tm.role = 'PRODUCT_OWNER' AND tm.isActive = true")
    Optional<TeamMember> findProductOwnerByTeamId(@Param("teamId") Long teamId);

    /**
     * Finds active members by role in a team.
     *
     * @param teamId The team ID
     * @param role   The Scrum role
     * @return List of team members with the role
     */
    List<TeamMember> findByTeamIdAndRoleAndIsActiveTrue(Long teamId, ScrumRole role);
}
