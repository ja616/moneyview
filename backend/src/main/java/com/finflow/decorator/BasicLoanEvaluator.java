package com.finflow.decorator;

import com.finflow.dto.EligibilityResponse;
import com.finflow.model.LoanApplication;
import com.finflow.strategy.EligibilityStrategy;
import com.finflow.strategy.EligibilityStrategyRegistry;
import org.springframework.stereotype.Component;

/**
 * ============================================================================
 * CONCRETE COMPONENT: BasicLoanEvaluator
 * ============================================================================
 * Performs the core business calculation by delegating to the appropriate
 * EligibilityStrategy. Has no logging or profiling overhead.
 */
@Component("basicLoanEvaluator")
public class BasicLoanEvaluator implements LoanEvaluator {

    private final EligibilityStrategyRegistry strategyRegistry;

    public BasicLoanEvaluator(EligibilityStrategyRegistry strategyRegistry) {
        this.strategyRegistry = strategyRegistry;
    }

    @Override
    public EligibilityResponse evaluate(LoanApplication application) {
        String loanType = application.getLoanProduct().getLoanType();
        EligibilityStrategy strategy = strategyRegistry.getStrategy(loanType);
        return strategy.evaluate(application.getCustomer(), application);
    }
}
