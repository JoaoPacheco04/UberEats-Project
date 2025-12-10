package com.eduscrum.upt.Ubereats.service;

import com.eduscrum.upt.Ubereats.dto.request.BadgeRequestDTO;
import com.eduscrum.upt.Ubereats.dto.response.BadgeResponseDTO;
import com.eduscrum.upt.Ubereats.entity.Badge;
import com.eduscrum.upt.Ubereats.entity.User;
import com.eduscrum.upt.Ubereats.entity.enums.BadgeType;
import com.eduscrum.upt.Ubereats.entity.enums.UserRole;
import com.eduscrum.upt.Ubereats.repository.BadgeRepository;
import com.eduscrum.upt.Ubereats.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for BadgeService.
 *
 * @author UberEats
 * @version 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BadgeServiceTest {

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private UserRepository userRepository;

    private User teacher;

    @BeforeEach
    void setUp() {
        badgeRepository.deleteAll();
        userRepository.deleteAll();

        // Create Teacher who will create badges
        teacher = new User();
        teacher.setFirstName("Prof");
        teacher.setLastName("Badge");
        teacher.setEmail("prof@badge.com");
        teacher.setUsername("profbadge");
        teacher.setPassword("password");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);
    }

    // ===================== CREATE BADGE TESTS =====================

    @Test
    void createBadge_ManualType_Success() {
        BadgeRequestDTO request = new BadgeRequestDTO();
        request.setName("Gold Star");
        request.setDescription("Awarded for excellence");
        request.setPoints(50);
        request.setBadgeType(BadgeType.MANUAL);
        request.setCreatedByUserId(teacher.getId());

        BadgeResponseDTO response = badgeService.createBadge(request);

        assertNotNull(response.getId());
        assertEquals("Gold Star", response.getName());
        assertEquals(50, response.getPoints());
        assertEquals(BadgeType.MANUAL, response.getBadgeType());
        assertTrue(response.getIsActive());
        assertEquals(teacher.getId(), response.getCreatedByUserId());
    }

    @Test
    void createBadge_AutomaticType_Success() {
        BadgeRequestDTO request = new BadgeRequestDTO();
        request.setName("Sprint Master");
        request.setDescription("Complete all sprints on time");
        request.setPoints(100);
        request.setBadgeType(BadgeType.AUTOMATIC);
        request.setTriggerCondition("ALL_SPRINTS_ON_TIME");
        request.setCreatedByUserId(teacher.getId());

        BadgeResponseDTO response = badgeService.createBadge(request);

        assertNotNull(response.getId());
        assertEquals(BadgeType.AUTOMATIC, response.getBadgeType());
        assertEquals("ALL_SPRINTS_ON_TIME", response.getTriggerCondition());
    }

    @Test
    void createBadge_WithOptionalFields_Success() {
        BadgeRequestDTO request = new BadgeRequestDTO();
        request.setName("Innovation Hero");
        request.setDescription("Innovative solution");
        request.setPoints(75);
        request.setBadgeType(BadgeType.MANUAL);
        request.setCreatedByUserId(teacher.getId());
        request.setIcon("star-icon");
        request.setColor("#FFD700");

        BadgeResponseDTO response = badgeService.createBadge(request);

        assertEquals("star-icon", response.getIcon());
        assertEquals("#FFD700", response.getColor());
    }

    @Test
    void createBadge_DuplicateName_ThrowsException() {
        // Create first badge
        BadgeRequestDTO request1 = new BadgeRequestDTO();
        request1.setName("Unique Badge");
        request1.setDescription("First badge");
        request1.setPoints(10);
        request1.setBadgeType(BadgeType.MANUAL);
        request1.setCreatedByUserId(teacher.getId());
        badgeService.createBadge(request1);

        // Try to create second with same name
        BadgeRequestDTO request2 = new BadgeRequestDTO();
        request2.setName("Unique Badge");
        request2.setDescription("Second badge");
        request2.setPoints(20);
        request2.setBadgeType(BadgeType.MANUAL);
        request2.setCreatedByUserId(teacher.getId());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            badgeService.createBadge(request2);
        });

        assertTrue(ex.getMessage().contains("already taken"));
    }

    @Test
    void createBadge_EmptyName_ThrowsException() {
        BadgeRequestDTO request = new BadgeRequestDTO();
        request.setName("");
        request.setDescription("Description");
        request.setPoints(10);
        request.setBadgeType(BadgeType.MANUAL);
        request.setCreatedByUserId(teacher.getId());

        assertThrows(IllegalArgumentException.class, () -> {
            badgeService.createBadge(request);
        });
    }

    @Test
    void createBadge_NullDescription_ThrowsException() {
        BadgeRequestDTO request = new BadgeRequestDTO();
        request.setName("Test Badge");
        request.setDescription(null);
        request.setPoints(10);
        request.setBadgeType(BadgeType.MANUAL);
        request.setCreatedByUserId(teacher.getId());

        assertThrows(IllegalArgumentException.class, () -> {
            badgeService.createBadge(request);
        });
    }

    @Test
    void createBadge_NegativePoints_ThrowsException() {
        BadgeRequestDTO request = new BadgeRequestDTO();
        request.setName("Test Badge");
        request.setDescription("Description");
        request.setPoints(-10);
        request.setBadgeType(BadgeType.MANUAL);
        request.setCreatedByUserId(teacher.getId());

        assertThrows(IllegalArgumentException.class, () -> {
            badgeService.createBadge(request);
        });
    }

    @Test
    void createBadge_NullBadgeType_ThrowsException() {
        BadgeRequestDTO request = new BadgeRequestDTO();
        request.setName("Test Badge");
        request.setDescription("Description");
        request.setPoints(10);
        request.setBadgeType(null);
        request.setCreatedByUserId(teacher.getId());

        assertThrows(IllegalArgumentException.class, () -> {
            badgeService.createBadge(request);
        });
    }

    @Test
    void createBadge_NullCreatedBy_ThrowsException() {
        BadgeRequestDTO request = new BadgeRequestDTO();
        request.setName("Test Badge");
        request.setDescription("Description");
        request.setPoints(10);
        request.setBadgeType(BadgeType.MANUAL);
        request.setCreatedByUserId(null);

        assertThrows(IllegalArgumentException.class, () -> {
            badgeService.createBadge(request);
        });
    }

    @Test
    void createBadge_InvalidCreatedBy_ThrowsException() {
        BadgeRequestDTO request = new BadgeRequestDTO();
        request.setName("Test Badge");
        request.setDescription("Description");
        request.setPoints(10);
        request.setBadgeType(BadgeType.MANUAL);
        request.setCreatedByUserId(999L);

        assertThrows(IllegalArgumentException.class, () -> {
            badgeService.createBadge(request);
        });
    }

    // ===================== GET BADGE TESTS =====================

    @Test
    void getBadgeById_Success() {
        BadgeRequestDTO request = new BadgeRequestDTO();
        request.setName("Find Me Badge");
        request.setDescription("Description");
        request.setPoints(25);
        request.setBadgeType(BadgeType.MANUAL);
        request.setCreatedByUserId(teacher.getId());
        BadgeResponseDTO created = badgeService.createBadge(request);

        Optional<BadgeResponseDTO> found = badgeService.getBadgeById(created.getId());

        assertTrue(found.isPresent());
        assertEquals("Find Me Badge", found.get().getName());
    }

    @Test
    void getBadgeById_NotFound() {
        Optional<BadgeResponseDTO> found = badgeService.getBadgeById(999L);

        assertTrue(found.isEmpty());
    }

    @Test
    void getBadgeByName_Success() {
        BadgeRequestDTO request = new BadgeRequestDTO();
        request.setName("Named Badge");
        request.setDescription("Description");
        request.setPoints(30);
        request.setBadgeType(BadgeType.MANUAL);
        request.setCreatedByUserId(teacher.getId());
        badgeService.createBadge(request);

        Optional<BadgeResponseDTO> found = badgeService.getBadgeByName("Named Badge");

        assertTrue(found.isPresent());
        assertEquals(30, found.get().getPoints());
    }

    @Test
    void getAllBadges_ReturnsAllBadges() {
        createTestBadge("Badge 1", 10);
        createTestBadge("Badge 2", 20);
        createTestBadge("Badge 3", 30);

        List<BadgeResponseDTO> badges = badgeService.getAllBadges();

        assertEquals(3, badges.size());
    }

    @Test
    void getActiveBadges_ReturnsOnlyActive() {
        BadgeResponseDTO active = createTestBadge("Active Badge", 10);
        BadgeResponseDTO toDeactivate = createTestBadge("Inactive Badge", 20);

        // Deactivate one
        badgeService.toggleBadgeStatus(toDeactivate.getId());

        List<BadgeResponseDTO> activeBadges = badgeService.getActiveBadges();

        assertEquals(1, activeBadges.size());
        assertEquals("Active Badge", activeBadges.get(0).getName());
    }

    @Test
    void getBadgesByType_ReturnsCorrectType() {
        createTestBadge("Manual 1", 10);

        BadgeRequestDTO autoRequest = new BadgeRequestDTO();
        autoRequest.setName("Auto Badge");
        autoRequest.setDescription("Automatic");
        autoRequest.setPoints(50);
        autoRequest.setBadgeType(BadgeType.AUTOMATIC);
        autoRequest.setCreatedByUserId(teacher.getId());
        badgeService.createBadge(autoRequest);

        List<BadgeResponseDTO> manualBadges = badgeService.getBadgesByType(BadgeType.MANUAL);
        List<BadgeResponseDTO> autoBadges = badgeService.getBadgesByType(BadgeType.AUTOMATIC);

        assertEquals(1, manualBadges.size());
        assertEquals(1, autoBadges.size());
        assertEquals("Auto Badge", autoBadges.get(0).getName());
    }

    @Test
    void getBadgesByCreator_ReturnsCorrectCreator() {
        createTestBadge("Creator Badge", 10);

        List<BadgeResponseDTO> badges = badgeService.getBadgesByCreator(teacher.getId());

        assertEquals(1, badges.size());
        assertEquals(teacher.getId(), badges.get(0).getCreatedByUserId());
    }

    // ===================== EXISTENCE CHECKS =====================

    @Test
    void existsByName_ReturnsTrue() {
        createTestBadge("Existing Badge", 10);

        assertTrue(badgeService.existsByName("Existing Badge"));
    }

    @Test
    void existsByName_ReturnsFalse() {
        assertFalse(badgeService.existsByName("Non Existing"));
    }

    @Test
    void existsById_ReturnsTrue() {
        BadgeResponseDTO badge = createTestBadge("Test Badge", 10);

        assertTrue(badgeService.existsById(badge.getId()));
    }

    @Test
    void existsById_ReturnsFalse() {
        assertFalse(badgeService.existsById(999L));
    }

    // ===================== UPDATE BADGE TESTS =====================

    @Test
    void updateBadge_Success() {
        BadgeResponseDTO created = createTestBadge("Original Name", 10);

        BadgeRequestDTO updateRequest = new BadgeRequestDTO();
        updateRequest.setName("Updated Name");
        updateRequest.setDescription("Updated Description");
        updateRequest.setPoints(100);
        updateRequest.setBadgeType(BadgeType.MANUAL);
        updateRequest.setCreatedByUserId(teacher.getId());

        BadgeResponseDTO updated = badgeService.updateBadge(created.getId(), updateRequest);

        assertEquals("Updated Name", updated.getName());
        assertEquals("Updated Description", updated.getDescription());
        assertEquals(100, updated.getPoints());
    }

    @Test
    void updateBadge_KeepSameName_Success() {
        BadgeResponseDTO created = createTestBadge("Same Name", 10);

        BadgeRequestDTO updateRequest = new BadgeRequestDTO();
        updateRequest.setName("Same Name");
        updateRequest.setDescription("Updated Description");
        updateRequest.setPoints(50);
        updateRequest.setBadgeType(BadgeType.MANUAL);
        updateRequest.setCreatedByUserId(teacher.getId());

        BadgeResponseDTO updated = badgeService.updateBadge(created.getId(), updateRequest);

        assertEquals("Same Name", updated.getName());
        assertEquals(50, updated.getPoints());
    }

    @Test
    void updateBadge_DuplicateName_ThrowsException() {
        createTestBadge("Existing Name", 10);
        BadgeResponseDTO toUpdate = createTestBadge("To Update", 20);

        BadgeRequestDTO updateRequest = new BadgeRequestDTO();
        updateRequest.setName("Existing Name");
        updateRequest.setDescription("Description");
        updateRequest.setPoints(30);
        updateRequest.setBadgeType(BadgeType.MANUAL);
        updateRequest.setCreatedByUserId(teacher.getId());

        assertThrows(IllegalArgumentException.class, () -> {
            badgeService.updateBadge(toUpdate.getId(), updateRequest);
        });
    }

    // ===================== TOGGLE STATUS TESTS =====================

    @Test
    void toggleBadgeStatus_DeactivatesBadge() {
        BadgeResponseDTO badge = createTestBadge("Toggle Badge", 10);
        assertTrue(badge.getIsActive());

        BadgeResponseDTO toggled = badgeService.toggleBadgeStatus(badge.getId());

        assertFalse(toggled.getIsActive());
    }

    @Test
    void toggleBadgeStatus_ActivatesBadge() {
        BadgeResponseDTO badge = createTestBadge("Toggle Badge", 10);
        badgeService.toggleBadgeStatus(badge.getId()); // Deactivate

        BadgeResponseDTO toggled = badgeService.toggleBadgeStatus(badge.getId()); // Activate

        assertTrue(toggled.getIsActive());
    }

    // ===================== DELETE BADGE TESTS =====================

    @Test
    void deleteBadge_Success() {
        BadgeResponseDTO badge = createTestBadge("Delete Me", 10);

        badgeService.deleteBadge(badge.getId());

        assertFalse(badgeService.existsById(badge.getId()));
    }

    @Test
    void deleteBadge_NotFound_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            badgeService.deleteBadge(999L);
        });
    }

    // ===================== CAN BE AWARDED TESTS =====================

    @Test
    void canBeAwarded_ActiveBadge_ReturnsTrue() {
        BadgeResponseDTO badge = createTestBadge("Active Badge", 10);

        assertTrue(badgeService.canBeAwarded(badge.getId()));
    }

    @Test
    void canBeAwarded_InactiveBadge_ReturnsFalse() {
        BadgeResponseDTO badge = createTestBadge("Inactive Badge", 10);
        badgeService.toggleBadgeStatus(badge.getId());

        assertFalse(badgeService.canBeAwarded(badge.getId()));
    }

    @Test
    void canBeAwarded_NonExistentBadge_ReturnsFalse() {
        assertFalse(badgeService.canBeAwarded(999L));
    }

    // ===================== AUTOMATIC BADGES TESTS =====================

    @Test
    void getAutomaticBadges_ReturnsOnlyActiveAutomatic() {
        createTestBadge("Manual Badge", 10);

        BadgeRequestDTO autoRequest = new BadgeRequestDTO();
        autoRequest.setName("Auto Active");
        autoRequest.setDescription("Automatic active");
        autoRequest.setPoints(50);
        autoRequest.setBadgeType(BadgeType.AUTOMATIC);
        autoRequest.setCreatedByUserId(teacher.getId());
        badgeService.createBadge(autoRequest);

        List<Badge> automaticBadges = badgeService.getAutomaticBadges();

        assertEquals(1, automaticBadges.size());
        assertEquals("Auto Active", automaticBadges.get(0).getName());
    }

    // ===================== BADGE AWARD COUNT TESTS =====================

    @Test
    void getBadgeAwardCount_NoAchievements_ReturnsZero() {
        BadgeResponseDTO badge = createTestBadge("No Awards", 10);

        Integer count = badgeService.getBadgeAwardCount(badge.getId());

        assertEquals(0, count);
    }

    @Test
    void getBadgeAwardCount_NonExistent_ReturnsZero() {
        Integer count = badgeService.getBadgeAwardCount(999L);

        assertEquals(0, count);
    }

    // ===================== HELPER METHODS =====================

    private BadgeResponseDTO createTestBadge(String name, int points) {
        BadgeRequestDTO request = new BadgeRequestDTO();
        request.setName(name);
        request.setDescription("Description for " + name);
        request.setPoints(points);
        request.setBadgeType(BadgeType.MANUAL);
        request.setCreatedByUserId(teacher.getId());
        return badgeService.createBadge(request);
    }
}
