package com.eduscrum.upt.Ubereats.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for custom exceptions.
 */
class ExceptionTest {

    // ===================== RESOURCE NOT FOUND EXCEPTION =====================

    @Test
    void resourceNotFoundException_WithMessage_ReturnsMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("User not found with id: 1");

        assertEquals("User not found with id: 1", ex.getMessage());
    }

    @Test
    void resourceNotFoundException_IsRuntimeException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Test");

        assertTrue(ex instanceof RuntimeException);
    }

    // ===================== BUSINESS LOGIC EXCEPTION =====================

    @Test
    void businessLogicException_WithMessage_ReturnsMessage() {
        BusinessLogicException ex = new BusinessLogicException("Cannot delete active course");

        assertEquals("Cannot delete active course", ex.getMessage());
    }

    @Test
    void businessLogicException_IsRuntimeException() {
        BusinessLogicException ex = new BusinessLogicException("Test");

        assertTrue(ex instanceof RuntimeException);
    }

    // ===================== EXCEPTION THROWING =====================

    @Test
    void resourceNotFoundException_CanBeThrown() {
        assertThrows(ResourceNotFoundException.class, () -> {
            throw new ResourceNotFoundException("Not found");
        });
    }

    @Test
    void businessLogicException_CanBeThrown() {
        assertThrows(BusinessLogicException.class, () -> {
            throw new BusinessLogicException("Logic error");
        });
    }
}
