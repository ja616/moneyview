package com.finflow.exception;

/**
 * ============================================================================
 * EXCEPTION: BusinessException
 * ============================================================================
 * Thrown when a business rule constraint is violated
 * (e.g. attempting to approve an already rejected application).
 * Maps to HTTP 400 BAD REQUEST.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
