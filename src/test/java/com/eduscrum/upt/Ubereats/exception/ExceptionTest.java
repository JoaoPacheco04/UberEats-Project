package com.eduscrum.upt.Ubereats.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for custom exception classes.
 * Tests ResourceNotFoundException and BusinessLogicException behavior.
 *
 * @author UberEats
 * @version 0.7.0
 */
class ExceptionTest {

    // region RESOURCE NOT FOUND EXCEPTION

    /** Tests that ResourceNotFoundException returns the correct message. */
    @Test
    void resourceNotFoundException_WithMessage_ReturnsMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("User not found with id: 1");

        assertEquals("User not found with id: 1", ex.getMessage());
    }

    /** Tests that ResourceNotFoundException is a RuntimeException. */
    @Test
    void resourceNotFoundException_IsRuntimeException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Test");

        assertTrue(ex instanceof RuntimeException);
    }

    // region BUSINESS LOGIC EXCEPTION

    /** Tests that BusinessLogicException returns the correct message. */
    @Test
    void businessLogicException_WithMessage_ReturnsMessage() {
        BusinessLogicException ex = new BusinessLogicException("Cannot delete active course");

        assertEquals("Cannot delete active course", ex.getMessage());
    }

    /** Tests that BusinessLogicException is a RuntimeException. */
    @Test
    void businessLogicException_IsRuntimeException() {
        BusinessLogicException ex = new BusinessLogicException("Test");

        assertTrue(ex instanceof RuntimeException);
    }

    // region EXCEPTION THROWING

    /** Tests that ResourceNotFoundException can be thrown and caught. */
    @Test
    void resourceNotFoundException_CanBeThrown() {
        assertThrows(ResourceNotFoundException.class, () -> {
            throw new ResourceNotFoundException("Not found");
        });
    }

    /** Tests that BusinessLogicException can be thrown and caught. */
    @Test
    void businessLogicException_CanBeThrown() {
        assertThrows(BusinessLogicException.class, () -> {
            throw new BusinessLogicException("Logic error");
        });
    }
}
