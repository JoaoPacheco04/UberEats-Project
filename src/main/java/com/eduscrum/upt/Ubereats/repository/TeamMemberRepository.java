package com.eduscrum.upt.Ubereats.repository;

import com.eduscrum.upt.Ubereats.entity.TeamMember;
import com.eduscrum.upt.Ubereats.entity.enums.ScrumRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    // Find active team members by team
    List<TeamMember> findByTeamIdAndIsActiveTrue(Long teamId);

    // Find team member by user and team
    Optional<TeamMember> findByUserIdAndTeamId(Long userId, Long teamId);

    // Find all teams where user is active member
    List<TeamMember> findByUserIdAndIsActiveTrue(Long userId);

    // Count active members in team
    Long countByTeamIdAndIsActiveTrue(Long teamId);

    // Find Scrum Master of a team
    @Query("SELECT tm FROM TeamMember tm WHERE tm.team.id = :teamId AND tm.role = 'SCRUM_MASTER' AND tm.isActive = true")
    Optional<TeamMember> findScrumMasterByTeamId(@Param("teamId") Long teamId);

    // Find Product Owner of a team
    @Query("SELECT tm FROM TeamMember tm WHERE tm.team.id = :teamId AND tm.role = 'PRODUCT_OWNER' AND tm.isActive = true")
    Optional<TeamMember> findProductOwnerByTeamId(@Param("teamId") Long teamId);

    // Find members by role in team
    List<TeamMember> findByTeamIdAndRoleAndIsActiveTrue(Long teamId, ScrumRole role);
}