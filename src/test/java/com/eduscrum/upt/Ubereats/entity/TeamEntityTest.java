package com.eduscrum.upt.Ubereats.entity;

import com.eduscrum.upt.Ubereats.entity.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure unit tests for Team entity business logic methods.
 */
class TeamEntityTest {

    private Team team;
    private User user;

    @BeforeEach
    void setUp() {
        team = new Team("Test Team");
        team.setId(1L);
        team.setMembers(new ArrayList<>());
        team.setProjects(new ArrayList<>());
        team.setTeamAchievements(new ArrayList<>());
        team.setAnalytics(new ArrayList<>());

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(UserRole.STUDENT);
    }

    // ===================== MEMBER COUNT TESTS =====================

    @Test
    void getMemberCount_NoMembers_ReturnsZero() {
        assertEquals(0, team.getMemberCount());
    }

    @Test
    void getMemberCount_WithActiveMembers_ReturnsCount() {
        TeamMember member1 = createMember(user, ScrumRole.DEVELOPER);
        TeamMember member2 = createMember(createUser(2L), ScrumRole.SCRUM_MASTER);
        team.getMembers().add(member1);
        team.getMembers().add(member2);

        assertEquals(2, team.getMemberCount());
    }

    @Test
    void getMemberCount_WithInactiveMembers_ExcludesInactive() {
        TeamMember activeMember = createMember(user, ScrumRole.DEVELOPER);
        TeamMember inactiveMember = createMember(createUser(2L), ScrumRole.DEVELOPER);
        inactiveMember.setIsActive(false);

        team.getMembers().add(activeMember);
        team.getMembers().add(inactiveMember);

        assertEquals(1, team.getMemberCount());
    }

    // ===================== SCRUM MASTER TESTS =====================

    @Test
    void getScrumMaster_NoScrumMaster_ReturnsNull() {
        TeamMember developer = createMember(user, ScrumRole.DEVELOPER);
        team.getMembers().add(developer);

        assertNull(team.getScrumMaster());
    }

    @Test
    void getScrumMaster_WithScrumMaster_ReturnsScrumMaster() {
        User scrumMaster = createUser(2L);
        TeamMember smMember = createMember(scrumMaster, ScrumRole.SCRUM_MASTER);
        team.getMembers().add(smMember);

        assertEquals(scrumMaster, team.getScrumMaster());
    }

    // ===================== PRODUCT OWNER TESTS =====================

    @Test
    void getProductOwner_NoProductOwner_ReturnsNull() {
        assertNull(team.getProductOwner());
    }

    @Test
    void getProductOwner_WithProductOwner_ReturnsProductOwner() {
        User po = createUser(2L);
        TeamMember poMember = createMember(po, ScrumRole.PRODUCT_OWNER);
        team.getMembers().add(poMember);

        assertEquals(po, team.getProductOwner());
    }

    // ===================== DEVELOPERS TESTS =====================

    @Test
    void getDevelopers_NoDevelopers_ReturnsEmptyList() {
        assertTrue(team.getDevelopers().isEmpty());
    }

    @Test
    void getDevelopers_WithDevelopers_ReturnsDevelopers() {
        TeamMember dev1 = createMember(user, ScrumRole.DEVELOPER);
        TeamMember dev2 = createMember(createUser(2L), ScrumRole.DEVELOPER);
        TeamMember sm = createMember(createUser(3L), ScrumRole.SCRUM_MASTER);

        team.getMembers().add(dev1);
        team.getMembers().add(dev2);
        team.getMembers().add(sm);

        assertEquals(2, team.getDevelopers().size());
    }

    // ===================== TOTAL POINTS TESTS =====================

    @Test
    void getTotalPoints_NoAchievements_ReturnsZero() {
        assertEquals(0, team.getTotalPoints());
    }

    @Test
    void getTotalPoints_WithAchievements_ReturnsSum() {
        Badge badge1 = createBadge("Badge1", 50);
        Badge badge2 = createBadge("Badge2", 30);

        Achievement ach1 = new Achievement();
        ach1.setBadge(badge1);
        Achievement ach2 = new Achievement();
        ach2.setBadge(badge2);

        team.getTeamAchievements().add(ach1);
        team.getTeamAchievements().add(ach2);

        assertEquals(80, team.getTotalPoints());
    }

    // ===================== ACTIVE MEMBERS TESTS =====================

    @Test
    void getActiveMembers_ReturnsOnlyActive() {
        TeamMember active = createMember(user, ScrumRole.DEVELOPER);
        TeamMember inactive = createMember(createUser(2L), ScrumRole.DEVELOPER);
        inactive.setIsActive(false);

        team.getMembers().add(active);
        team.getMembers().add(inactive);

        assertEquals(1, team.getActiveMembers().size());
    }

    // ===================== HAS MEMBER TESTS =====================

    @Test
    void hasMember_UserNotInTeam_ReturnsFalse() {
        assertFalse(team.hasMember(user));
    }

    @Test
    void hasMember_UserInTeam_ReturnsTrue() {
        TeamMember member = createMember(user, ScrumRole.DEVELOPER);
        team.getMembers().add(member);

        assertTrue(team.hasMember(user));
    }

    // ===================== EQUALS AND HASHCODE TESTS =====================

    @Test
    void equals_SameIdAndName_ReturnsTrue() {
        Team team2 = new Team("Test Team");
        team2.setId(1L);

        assertEquals(team, team2);
    }

    @Test
    void equals_DifferentId_ReturnsFalse() {
        Team team2 = new Team("Test Team");
        team2.setId(2L);

        assertNotEquals(team, team2);
    }

    @Test
    void equals_DifferentName_ReturnsFalse() {
        Team team2 = new Team("Different Name");
        team2.setId(1L);

        assertNotEquals(team, team2);
    }

    @Test
    void hashCode_SameIdAndName_SameHash() {
        Team team2 = new Team("Test Team");
        team2.setId(1L);

        assertEquals(team.hashCode(), team2.hashCode());
    }

    // ===================== TOSTRING TESTS =====================

    @Test
    void toString_ContainsName() {
        String str = team.toString();
        assertTrue(str.contains("Test Team"));
    }

    // ===================== HELPER METHODS =====================

    private TeamMember createMember(User u, ScrumRole role) {
        TeamMember member = new TeamMember();
        member.setTeam(team);
        member.setUser(u);
        member.setRole(role);
        member.setIsActive(true);
        return member;
    }

    private User createUser(Long id) {
        User u = new User();
        u.setId(id);
        u.setUsername("user" + id);
        u.setEmail("user" + id + "@test.com");
        u.setFirstName("First");
        u.setLastName("Last");
        u.setRole(UserRole.STUDENT);
        return u;
    }

    private Badge createBadge(String name, int points) {
        Badge badge = new Badge();
        badge.setName(name);
        badge.setPoints(points);
        return badge;
    }
}
