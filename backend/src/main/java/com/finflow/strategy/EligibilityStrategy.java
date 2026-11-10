package com.finflow.strategy;

import com.finflow.dto.EligibilityResponse;
import com.finflow.model.Customer;
import com.finflow.model.LoanApplication;

/**
 * ============================================================================
 * STRATEGY PATTERN: EligibilityStrategy
 * ============================================================================
 * Defines the contract for interchangeable eligibility evaluation algorithms.
 */
public interface EligibilityStrategy {

    EligibilityResponse evaluate(Customer customer, LoanApplication application);

    String getSupportedLoanType();
}
