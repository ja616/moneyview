package com.finflow.strategy;

import com.finflow.dto.EligibilityResponse;
import com.finflow.model.Customer;
import com.finflow.model.LoanApplication;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * ============================================================================
 * STRATEGY: EducationLoanEligibilityStrategy
 * ============================================================================
 * Underwriting criteria:
 * - Minimum household/co-applicant income: ₹20,000.00
 * - Includes student grace period terms.
 */
@Component
public class EducationLoanEligibilityStrategy implements EligibilityStrategy {

    private static final BigDecimal MIN_INCOME = new BigDecimal("20000.00");

    @Override
    public EligibilityResponse evaluate(Customer customer, LoanApplication application) {
        BigDecimal income = customer.getMonthlyIncome();
        BigDecimal requested = application.getRequestedAmount();
        int tenure = application.getTenureMonths();

        BigDecimal approxEmi = requested.divide(BigDecimal.valueOf(tenure), 2, RoundingMode.HALF_EVEN);

        if (income.compareTo(MIN_INCOME) < 0) {
            return new EligibilityResponse(
                    application.getId(),
                    false,
                    BigDecimal.ZERO,
                    approxEmi,
                    "Applicant/co-applicant income (₹" + income + ") is below the required ₹" + MIN_INCOME + " threshold"
            );
        }

        return new EligibilityResponse(
                application.getId(),
                true,
                requested,
                approxEmi,
                "Applicant approved for Education Loan with grace period terms during study duration."
        );
    }

    @Override
    public String getSupportedLoanType() {
        return "EDUCATION";
    }
}
