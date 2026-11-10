package com.finflow.strategy;

import com.finflow.dto.EligibilityResponse;
import com.finflow.model.Customer;
import com.finflow.model.LoanApplication;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * ============================================================================
 * STRATEGY: PersonalLoanEligibilityStrategy
 * ============================================================================
 * Underwriting criteria:
 * - Minimum monthly income: ₹25,000.00
 * - Maximum EMI cap: 40% of monthly income
 */
@Component
public class PersonalLoanEligibilityStrategy implements EligibilityStrategy {

    private static final BigDecimal MIN_INCOME = new BigDecimal("25000.00");
    private static final BigDecimal DTI_CAP = new BigDecimal("0.40");

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
                    "Applicant monthly income (₹" + income + ") is below the minimum personal loan threshold of ₹" + MIN_INCOME
            );
        }

        BigDecimal maxAllowedEmi = income.multiply(DTI_CAP);
        if (approxEmi.compareTo(maxAllowedEmi) > 0) {
            BigDecimal maxAllowedAmount = maxAllowedEmi.multiply(BigDecimal.valueOf(tenure));
            return new EligibilityResponse(
                    application.getId(),
                    false,
                    maxAllowedAmount,
                    approxEmi,
                    "Requested EMI (₹" + approxEmi + ") exceeds the 40% debt-to-income cap (₹" + maxAllowedEmi + ")"
            );
        }

        return new EligibilityResponse(
                application.getId(),
                true,
                requested,
                approxEmi,
                "Applicant qualifies for Personal Loan with strong income-to-obligation ratio."
        );
    }

    @Override
    public String getSupportedLoanType() {
        return "PERSONAL";
    }
}
