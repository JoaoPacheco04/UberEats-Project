package com.eduscrum.upt.Ubereats.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GlobalExceptionHandler.
 */
/**
 * Tests for global exception handler.
 *
 * @author UberEats
 * @version 0.7.1
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private WebRequest webRequest;

    // ===================== RESOURCE NOT FOUND EXCEPTION =====================

    @Test
    void handleResourceNotFoundException_ReturnsNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("User not found");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/users/1");

        ResponseEntity<Object> response = exceptionHandler.handleResourceNotFoundException(ex, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(404, body.get("status"));
        assertEquals("Resource Not Found", body.get("error"));
        assertEquals("User not found", body.get("message"));
    }

    // ===================== BUSINESS LOGIC EXCEPTION =====================

    @Test
    void handleBusinessLogicException_ReturnsBadRequest() {
        BusinessLogicException ex = new BusinessLogicException("Invalid operation");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/courses");

        ResponseEntity<Object> response = exceptionHandler.handleBusinessLogicException(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(400, body.get("status"));
        assertEquals("Business Logic Error", body.get("error"));
        assertEquals("Invalid operation", body.get("message"));
    }

    // ===================== VALIDATION EXCEPTION =====================

    @Test
    void handleValidationExceptions_ReturnsBadRequestWithErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "email", "must not be null");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));

        ResponseEntity<Object> response = exceptionHandler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(400, body.get("status"));
        assertEquals("Validation Error", body.get("error"));
    }

    // ===================== GLOBAL EXCEPTION =====================

    @Test
    void handleGlobalException_ReturnsInternalServerError() {
        Exception ex = new RuntimeException("Unexpected error");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");

        ResponseEntity<Object> response = exceptionHandler.handleGlobalException(ex, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(500, body.get("status"));
        assertEquals("Internal Server Error", body.get("error"));
    }

    // ===================== MISSING PARAMETER EXCEPTION =====================

    @Test
    void handleMissingParams_ReturnsBadRequest() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("userId", "Long");

        ResponseEntity<Object> response = exceptionHandler.handleMissingParams(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(400, body.get("status"));
        assertEquals("Missing Parameter", body.get("error"));
        assertTrue(body.get("message").toString().contains("userId"));
    }
}
