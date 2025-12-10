package com.eduscrum.upt.Ubereats.entity;

import com.eduscrum.upt.Ubereats.entity.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure unit tests for TeamMember entity.
 */
/**
 * Unit tests for TeamMember entity.
 *
 * @version 0.5.0 (2025-11-05)
 */
class TeamMemberEntityTest {

    private TeamMember teamMember;
    private Team team;
    private User user;

    @BeforeEach
    void setUp() {
        team = new Team("Test Team");
        team.setId(1L);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(UserRole.STUDENT);

        teamMember = new TeamMember();
        teamMember.setId(1L);
        teamMember.setTeam(team);
        teamMember.setUser(user);
        teamMember.setRole(ScrumRole.DEVELOPER);
        teamMember.setIsActive(true);
    }

    // ===================== ROLE CHECKS =====================

    @Test
    void isScrumMaster_WhenScrumMaster_ReturnsTrue() {
        teamMember.setRole(ScrumRole.SCRUM_MASTER);
        assertTrue(teamMember.isScrumMaster());
    }

    @Test
    void isScrumMaster_WhenDeveloper_ReturnsFalse() {
        teamMember.setRole(ScrumRole.DEVELOPER);
        assertFalse(teamMember.isScrumMaster());
    }

    @Test
    void isProductOwner_WhenProductOwner_ReturnsTrue() {
        teamMember.setRole(ScrumRole.PRODUCT_OWNER);
        assertTrue(teamMember.isProductOwner());
    }

    @Test
    void isProductOwner_WhenDeveloper_ReturnsFalse() {
        teamMember.setRole(ScrumRole.DEVELOPER);
        assertFalse(teamMember.isProductOwner());
    }

    @Test
    void isDeveloper_WhenDeveloper_ReturnsTrue() {
        teamMember.setRole(ScrumRole.DEVELOPER);
        assertTrue(teamMember.isDeveloper());
    }

    @Test
    void isDeveloper_WhenScrumMaster_ReturnsFalse() {
        teamMember.setRole(ScrumRole.SCRUM_MASTER);
        assertFalse(teamMember.isDeveloper());
    }

    // ===================== ACTIVE MEMBER CHECKS =====================

    @Test
    void isActiveMember_WhenActiveAndNoLeftAt_ReturnsTrue() {
        teamMember.setIsActive(true);
        teamMember.setLeftAt(null);
        assertTrue(teamMember.isActiveMember());
    }

    @Test
    void isActiveMember_WhenNotActive_ReturnsFalse() {
        teamMember.setIsActive(false);
        assertFalse(teamMember.isActiveMember());
    }

    // ===================== LEAVE TEAM =====================

    @Test
    void leaveTeam_SetsInactiveAndLeftAt() {
        teamMember.leaveTeam();

        assertFalse(teamMember.getIsActive());
        assertNotNull(teamMember.getLeftAt());
    }

    // ===================== GETTERS =====================

    @Test
    void getTeam_ReturnsTeam() {
        assertEquals(team, teamMember.getTeam());
    }

    @Test
    void getUser_ReturnsUser() {
        assertEquals(user, teamMember.getUser());
    }

    @Test
    void getRole_ReturnsRole() {
        assertEquals(ScrumRole.DEVELOPER, teamMember.getRole());
    }

    @Test
    void getIsActive_ReturnsActiveStatus() {
        assertTrue(teamMember.getIsActive());
    }

    // ===================== EQUALS AND HASHCODE =====================

    @Test
    void equals_SameIdTeamUser_ReturnsTrue() {
        TeamMember member2 = new TeamMember();
        member2.setId(1L);
        member2.setTeam(team);
        member2.setUser(user);

        assertEquals(teamMember, member2);
    }

    @Test
    void equals_DifferentId_ReturnsFalse() {
        TeamMember member2 = new TeamMember();
        member2.setId(2L);
        member2.setTeam(team);
        member2.setUser(user);

        assertNotEquals(teamMember, member2);
    }

    // ===================== TOSTRING =====================

    @Test
    void toString_ContainsRole() {
        String str = teamMember.toString();
        assertTrue(str.contains("DEVELOPER"));
    }
}
