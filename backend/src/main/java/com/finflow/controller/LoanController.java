package com.finflow.controller;

import com.finflow.dto.CreateApplicationRequest;
import com.finflow.dto.EligibilityResponse;
import com.finflow.dto.LoanApplicationResponse;
import com.finflow.dto.LoanProductResponse;
import com.finflow.observer.LoanActivityObserver;
import com.finflow.observer.LoanNotificationObserver;
import com.finflow.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ============================================================================
 * CONTROLLER: LoanController (Customer REST Endpoints)
 * ============================================================================
 */
@RestController
@RequestMapping("/api/loans")
@CrossOrigin(origins = "*")
public class LoanController {

    private final LoanService loanService;

    // Constructor-based Dependency Injection
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping("/products")
    public ResponseEntity<List<LoanProductResponse>> getProducts() {
        return ResponseEntity.ok(loanService.getAllProducts());
    }

    @PostMapping("/applications")
    public ResponseEntity<LoanApplicationResponse> createApplication(@Valid @RequestBody CreateApplicationRequest request) {
        LoanApplicationResponse response = loanService.createApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/applications")
    public ResponseEntity<List<LoanApplicationResponse>> getAllApplications() {
        return ResponseEntity.ok(loanService.getAllApplications());
    }

    @GetMapping("/applications/{id}")
    public ResponseEntity<LoanApplicationResponse> getApplicationById(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.getApplicationById(id));
    }

    @PostMapping("/applications/{id}/evaluate")
    public ResponseEntity<EligibilityResponse> evaluateApplication(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.evaluateApplication(id));
    }

    /**
     * OBSERVER PATTERN: Retrieves audit activity trail for an application.
     */
    @GetMapping("/applications/{id}/activities")
    public ResponseEntity<List<LoanActivityObserver.ActivityLogEntry>> getApplicationActivities(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.getActivities(id));
    }

    /**
     * OBSERVER PATTERN: Retrieves customer in-app notifications for an application.
     */
    @GetMapping("/applications/{id}/notifications")
    public ResponseEntity<List<LoanNotificationObserver.NotificationEntry>> getApplicationNotifications(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.getNotifications(id));
    }

    /**
     * SINGLETON PATTERN: Proves shared instance reuse across requests.
     */
    @GetMapping("/diagnostic/singleton")
    public ResponseEntity<Map<String, Object>> getSingletonDiagnostic() {
        Map<String, Object> diagnostic = new HashMap<>();
        diagnostic.put("beanClass", loanService.getClass().getName());
        diagnostic.put("scope", "SINGLETON (Spring default)");
        diagnostic.put("identityHashCode", loanService.getBeanIdentityHashCode());
        diagnostic.put("explanation", "Every request returns the identical identityHashCode, proving Spring reuses the same singleton instance.");
        return ResponseEntity.ok(diagnostic);
    }
}
