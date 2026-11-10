package com.finflow.exception;

/**
 * ============================================================================
 * EXCEPTION: ResourceNotFoundException
 * ============================================================================
 * Thrown when an entity (Application, Product, Customer) is looked up by ID
 * but does not exist in persistence. Maps to HTTP 404 NOT FOUND.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
