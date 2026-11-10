package com.finflow.dto;

import java.math.BigDecimal;

/**
 * ============================================================================
 * DTO: EligibilityResponse
 * ============================================================================
 * Outcome returned after evaluating an applicant's financial profile.
 */
public class EligibilityResponse {

    private Long applicationId;
    private boolean eligible;
    private BigDecimal maxEligibleAmount;
    private BigDecimal calculatedEmi;
    private String decisionReason;

    public EligibilityResponse() {}

    public EligibilityResponse(Long applicationId, boolean eligible, BigDecimal maxEligibleAmount,
                               BigDecimal calculatedEmi, String decisionReason) {
        this.applicationId = applicationId;
        this.eligible = eligible;
        this.maxEligibleAmount = maxEligibleAmount;
        this.calculatedEmi = calculatedEmi;
        this.decisionReason = decisionReason;
    }

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public boolean isEligible() { return eligible; }
    public void setEligible(boolean eligible) { this.eligible = eligible; }

    public BigDecimal getMaxEligibleAmount() { return maxEligibleAmount; }
    public void setMaxEligibleAmount(BigDecimal maxEligibleAmount) { this.maxEligibleAmount = maxEligibleAmount; }

    public BigDecimal getCalculatedEmi() { return calculatedEmi; }
    public void setCalculatedEmi(BigDecimal calculatedEmi) { this.calculatedEmi = calculatedEmi; }

    public String getDecisionReason() { return decisionReason; }
    public void setDecisionReason(String decisionReason) { this.decisionReason = decisionReason; }
}
