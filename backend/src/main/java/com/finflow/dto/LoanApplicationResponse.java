package com.finflow.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * DTO: LoanApplicationResponse
 * ============================================================================
 * The JSON payload returned to clients representing an active or completed loan application.
 */
public class LoanApplicationResponse {

    private Long id;
    private String customerName;
    private String customerEmail;
    private String loanType;
    private BigDecimal requestedAmount;
    private Integer tenureMonths;
    private String eligibilityStatus;
    private String applicationStatus;
    private String notes;
    private LocalDateTime createdAt;

    public LoanApplicationResponse() {}

    public LoanApplicationResponse(Long id, String customerName, String customerEmail, String loanType,
                                   BigDecimal requestedAmount, Integer tenureMonths, String eligibilityStatus,
                                   String applicationStatus, String notes, LocalDateTime createdAt) {
        this.id = id;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.loanType = loanType;
        this.requestedAmount = requestedAmount;
        this.tenureMonths = tenureMonths;
        this.eligibilityStatus = eligibilityStatus;
        this.applicationStatus = applicationStatus;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }

    public BigDecimal getRequestedAmount() { return requestedAmount; }
    public void setRequestedAmount(BigDecimal requestedAmount) { this.requestedAmount = requestedAmount; }

    public Integer getTenureMonths() { return tenureMonths; }
    public void setTenureMonths(Integer tenureMonths) { this.tenureMonths = tenureMonths; }

    public String getEligibilityStatus() { return eligibilityStatus; }
    public void setEligibilityStatus(String eligibilityStatus) { this.eligibilityStatus = eligibilityStatus; }

    public String getApplicationStatus() { return applicationStatus; }
    public void setApplicationStatus(String applicationStatus) { this.applicationStatus = applicationStatus; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
