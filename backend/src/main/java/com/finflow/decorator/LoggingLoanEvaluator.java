package com.finflow.decorator;

import com.finflow.dto.EligibilityResponse;
import com.finflow.model.LoanApplication;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * ============================================================================
 * CONCRETE DECORATOR: LoggingLoanEvaluator
 * ============================================================================
 * Implements LoanEvaluator while holding a reference to another LoanEvaluator.
 * Adds performance profiling and logging transparently without modifying
 * the basic evaluator or underlying strategy algorithms.
 */
@Component("loggingLoanEvaluator")
@Primary
public class LoggingLoanEvaluator implements LoanEvaluator {

    private final LoanEvaluator delegate;

    // Injects the basic evaluator as the wrapped delegate
    public LoggingLoanEvaluator(@Qualifier("basicLoanEvaluator") LoanEvaluator delegate) {
        this.delegate = delegate;
    }

    @Override
    public EligibilityResponse evaluate(LoanApplication application) {
        long startNanos = System.nanoTime();
        System.out.printf("  [DECORATOR] >>> Starting eligibility evaluation for App #%d (Customer: %s, Type: %s, Amount: ₹%s)%n",
                application.getId(),
                application.getCustomer().getName(),
                application.getLoanProduct().getLoanType(),
                application.getRequestedAmount());

        // Delegate to the wrapped evaluator
        EligibilityResponse response = delegate.evaluate(application);

        long durationNanos = System.nanoTime() - startNanos;
        double durationMillis = durationNanos / 1_000_000.0;

        System.out.printf("  [DECORATOR] <<< Completed evaluation for App #%d in %.3f ms | Outcome: %s | Reason: %s%n",
                application.getId(),
                durationMillis,
                response.isEligible() ? "APPROVED / ELIGIBLE" : "INELIGIBLE",
                response.getDecisionReason());

        return response;
    }
}
