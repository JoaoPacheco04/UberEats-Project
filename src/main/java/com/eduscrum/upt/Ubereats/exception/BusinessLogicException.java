package com.eduscrum.upt.Ubereats.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when business logic validation fails.
 *
 * @author Joao
 * @author Ana
 * @version 0.4.0
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessLogicException extends RuntimeException {
    /**
     * Constructs a new BusinessLogicException with the specified message.
     *
     * @param message The detail message
     */
    public BusinessLogicException(String message) {
        super(message);
    }

    /**
     * Constructs a new BusinessLogicException with the specified message and cause.
     *
     * @param message The detail message
     * @param cause   The cause of this exception
     */
    public BusinessLogicException(String message, Throwable cause) {
        super(message, cause);
    }
}
