package com.finflow.decorator;

import com.finflow.dto.EligibilityResponse;
import com.finflow.model.LoanApplication;

/**
 * ============================================================================
 * DECORATOR PATTERN: LoanEvaluator Interface
 * ============================================================================
 * Defines the core evaluation component interface.
 */
public interface LoanEvaluator {

    EligibilityResponse evaluate(LoanApplication application);
}
