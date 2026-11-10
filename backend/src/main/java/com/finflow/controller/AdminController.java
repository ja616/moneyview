package com.finflow.controller;

import com.finflow.dto.LoanApplicationResponse;
import com.finflow.dto.RejectApplicationRequest;
import com.finflow.service.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ============================================================================
 * CONTROLLER: AdminController (Administrator Review & Decisions)
 * ============================================================================
 * Handles administrative actions: approving or rejecting applications.
 */
@RestController
@RequestMapping("/api/admin/applications")
@CrossOrigin(origins = "*")
public class AdminController {

    private final LoanService loanService;

    public AdminController(LoanService loanService) {
        this.loanService = loanService;
    }

    /**
     * PATCH /api/admin/applications/{id}/approve
     * Marks an application as APPROVED.
     */
    @PatchMapping("/{id}/approve")
    public ResponseEntity<LoanApplicationResponse> approveApplication(@PathVariable Long id) {
        LoanApplicationResponse response = loanService.approveApplication(id);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/admin/applications/{id}/reject
     * Marks an application as REJECTED with an optional reason.
     */
    @PatchMapping("/{id}/reject")
    public ResponseEntity<LoanApplicationResponse> rejectApplication(
            @PathVariable Long id,
            @RequestBody(required = false) RejectApplicationRequest request) {
        String reason = (request != null) ? request.getReason() : null;
        LoanApplicationResponse response = loanService.rejectApplication(id, reason);
        return ResponseEntity.ok(response);
    }
}
