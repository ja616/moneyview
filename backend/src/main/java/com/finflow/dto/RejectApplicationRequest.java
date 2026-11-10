package com.finflow.dto;

/**
 * ============================================================================
 * DTO: RejectApplicationRequest
 * ============================================================================
 * Optional payload when an admin rejects an application, carrying a rejection reason.
 */
public class RejectApplicationRequest {

    private String reason;

    public RejectApplicationRequest() {}

    public RejectApplicationRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
