package com.finflow.observer;

import com.finflow.model.LoanApplication;

/**
 * ============================================================================
 * OBSERVER PATTERN: LoanObserver Interface
 * ============================================================================
 * Observers implement this interface to receive state change broadcasts.
 */
public interface LoanObserver {

    void onStatusChanged(LoanApplication application, String previousStatus, String newStatus);
}
