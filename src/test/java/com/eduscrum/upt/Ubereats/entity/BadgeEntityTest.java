package com.eduscrum.upt.Ubereats.entity;

import com.eduscrum.upt.Ubereats.entity.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure unit tests for Badge entity business logic methods.
 */
class BadgeEntityTest {

    private Badge badge;
    private User teacher;

    @BeforeEach
    void setUp() {
        teacher = new User();
        teacher.setId(1L);
        teacher.setUsername("teacher");
        teacher.setRole(UserRole.TEACHER);

        badge = new Badge("Gold Star", "Excellence badge", 50, BadgeType.MANUAL, teacher);
        badge.setId(1L);
        badge.setIsActive(true);
    }

    // ===================== CONSTRUCTOR TESTS =====================

    @Test
    void constructor_SetsAllFields() {
        Badge newBadge = new Badge("Test Badge", "Description", 100, BadgeType.AUTOMATIC, teacher);

        assertEquals("Test Badge", newBadge.getName());
        assertEquals("Description", newBadge.getDescription());
        assertEquals(100, newBadge.getPoints());
        assertEquals(BadgeType.AUTOMATIC, newBadge.getBadgeType());
        assertEquals(teacher, newBadge.getCreatedBy());
        assertTrue(newBadge.getIsActive());
    }

    // ===================== IS ACTIVE TESTS =====================

    @Test
    void getIsActive_WhenTrue_ReturnsTrue() {
        badge.setIsActive(true);
        assertTrue(badge.getIsActive());
    }

    @Test
    void getIsActive_WhenFalse_ReturnsFalse() {
        badge.setIsActive(false);
        assertFalse(badge.getIsActive());
    }

    // ===================== IS MANUAL TESTS =====================

    @Test
    void isManual_WhenManualType_ReturnsTrue() {
        badge.setBadgeType(BadgeType.MANUAL);
        assertTrue(badge.isManual());
    }

    @Test
    void isManual_WhenAutomaticType_ReturnsFalse() {
        badge.setBadgeType(BadgeType.AUTOMATIC);
        assertFalse(badge.isManual());
    }

    // ===================== IS AUTOMATIC TESTS =====================

    @Test
    void isAutomatic_WhenAutomaticType_ReturnsTrue() {
        badge.setBadgeType(BadgeType.AUTOMATIC);
        assertTrue(badge.isAutomatic());
    }

    @Test
    void isAutomatic_WhenManualType_ReturnsFalse() {
        badge.setBadgeType(BadgeType.MANUAL);
        assertFalse(badge.isAutomatic());
    }

    // ===================== CAN BE AWARDED TESTS =====================

    @Test
    void canBeAwarded_WhenActive_ReturnsTrue() {
        badge.setIsActive(true);
        assertTrue(badge.canBeAwarded());
    }

    @Test
    void canBeAwarded_WhenNotActive_ReturnsFalse() {
        badge.setIsActive(false);
        assertFalse(badge.canBeAwarded());
    }

    // ===================== GETTERS AND SETTERS =====================

    @Test
    void settersAndGetters_WorkCorrectly() {
        badge.setName("Updated Name");
        badge.setDescription("Updated Description");
        badge.setPoints(75);
        badge.setIcon("⭐");
        badge.setColor("#00FF00");

        assertEquals("Updated Name", badge.getName());
        assertEquals("Updated Description", badge.getDescription());
        assertEquals(75, badge.getPoints());
        assertEquals("⭐", badge.getIcon());
        assertEquals("#00FF00", badge.getColor());
    }

    // ===================== EQUALS AND HASHCODE TESTS =====================

    @Test
    void equals_SameIdAndName_ReturnsTrue() {
        Badge badge2 = new Badge("Gold Star", "Desc", 100, BadgeType.AUTOMATIC, teacher);
        badge2.setId(1L);

        assertEquals(badge, badge2);
    }

    @Test
    void equals_DifferentId_ReturnsFalse() {
        Badge badge2 = new Badge("Gold Star", "Excellence badge", 50, BadgeType.MANUAL, teacher);
        badge2.setId(2L);

        assertNotEquals(badge, badge2);
    }

    @Test
    void hashCode_SameIdAndName_SameHash() {
        Badge badge2 = new Badge("Gold Star", "Desc", 100, BadgeType.AUTOMATIC, teacher);
        badge2.setId(1L);

        assertEquals(badge.hashCode(), badge2.hashCode());
    }

    // ===================== TOSTRING TESTS =====================

    @Test
    void toString_ContainsName() {
        String str = badge.toString();
        assertTrue(str.contains("Gold Star"));
    }

    @Test
    void toString_ContainsPoints() {
        String str = badge.toString();
        assertTrue(str.contains("50"));
    }
}
