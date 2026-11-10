package com.finflow.factory;

import com.finflow.model.LoanProduct;

/**
 * ============================================================================
 * FACTORY PATTERN: LoanProductFactory
 * ============================================================================
 * Common abstraction for loan product factories.
 * Decouples object instantiation, default limits, and rates from services.
 */
public interface LoanProductFactory {

    LoanProduct createProduct();

    String getSupportedLoanType();
}
