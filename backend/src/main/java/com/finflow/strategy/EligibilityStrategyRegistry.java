package com.finflow.strategy;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ============================================================================
 * STRATEGY REGISTRY: EligibilityStrategyRegistry
 * ============================================================================
 * Dynamically resolves the appropriate strategy at runtime without if-else ladders.
 */
@Component
public class EligibilityStrategyRegistry {

    private final Map<String, EligibilityStrategy> strategyMap = new HashMap<>();

    public EligibilityStrategyRegistry(List<EligibilityStrategy> strategies) {
        for (EligibilityStrategy strategy : strategies) {
            strategyMap.put(strategy.getSupportedLoanType().toUpperCase(), strategy);
        }
        System.out.printf("  [STRATEGY REGISTRY] Registered %d EligibilityStrategy beans: %s%n",
                strategyMap.size(), strategyMap.keySet());
    }

    public EligibilityStrategy getStrategy(String loanType) {
        EligibilityStrategy strategy = strategyMap.get(loanType.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("No eligibility strategy found for loan type: " + loanType);
        }
        return strategy;
    }
}
