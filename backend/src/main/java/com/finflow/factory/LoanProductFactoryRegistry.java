package com.finflow.factory;

import com.finflow.model.LoanProduct;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ============================================================================
 * FACTORY REGISTRY: LoanProductFactoryRegistry
 * ============================================================================
 * Leverages Spring's Dependency Injection to automatically discover all
 * LoanProductFactory components and register them into an extensible lookup map.
 *
 * Demonstrates Open/Closed Principle:
 * Adding a new loan product requires only adding a new @Component implementing
 * LoanProductFactory. This registry requires zero code modifications!
 */
@Component
public class LoanProductFactoryRegistry {

    private final Map<String, LoanProductFactory> factoryMap = new HashMap<>();

    public LoanProductFactoryRegistry(List<LoanProductFactory> factories) {
        for (LoanProductFactory factory : factories) {
            factoryMap.put(factory.getSupportedLoanType().toUpperCase(), factory);
        }
        System.out.printf("  [FACTORY REGISTRY] Registered %d LoanProductFactory beans: %s%n",
                factoryMap.size(), factoryMap.keySet());
    }

    public LoanProduct createProduct(String loanType) {
        LoanProductFactory factory = factoryMap.get(loanType.toUpperCase());
        if (factory == null) {
            throw new IllegalArgumentException("No factory registered for loan type: " + loanType);
        }
        return factory.createProduct();
    }

    public Collection<LoanProductFactory> getAllFactories() {
        return Collections.unmodifiableCollection(factoryMap.values());
    }

    public boolean supports(String loanType) {
        return factoryMap.containsKey(loanType.toUpperCase());
    }
}
