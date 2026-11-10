package com.finflow.observer;

import com.finflow.model.LoanApplication;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * SUBJECT (OBSERVABLE): LoanApplicationSubject
 * ============================================================================
 * Maintains the list of observers and broadcasts events whenever an application
 * changes state.
 */
@Component
public class LoanApplicationSubject {

    private final List<LoanObserver> observers = new ArrayList<>();

    // Spring auto-wires all available LoanObserver beans here!
    public LoanApplicationSubject(List<LoanObserver> observerBeans) {
        this.observers.addAll(observerBeans);
        System.out.printf("  [OBSERVER SUBJECT] Registered %d observers: %s%n",
                observers.size(), observers.stream().map(o -> o.getClass().getSimpleName()).toList());
    }

    public void addObserver(LoanObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(LoanObserver observer) {
        observers.remove(observer);
    }

    public void notifyStatusChange(LoanApplication application, String previousStatus, String newStatus) {
        for (LoanObserver observer : observers) {
            try {
                observer.onStatusChanged(application, previousStatus, newStatus);
            } catch (Exception e) {
                System.err.printf("  [OBSERVER SUBJECT] Error in observer %s: %s%n",
                        observer.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
}
