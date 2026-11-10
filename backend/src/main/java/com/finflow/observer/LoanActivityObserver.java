package com.finflow.observer;

import com.finflow.model.LoanApplication;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ============================================================================
 * CONCRETE OBSERVER: LoanActivityObserver
 * ============================================================================
 * Records timestamped activity logs whenever a loan's status changes.
 */
@Component
public class LoanActivityObserver implements LoanObserver {

    public record ActivityLogEntry(Long applicationId, String fromStatus, String toStatus, String message, LocalDateTime timestamp) {}

    private final Map<Long, List<ActivityLogEntry>> activityRegistry = new ConcurrentHashMap<>();

    @Override
    public void onStatusChanged(LoanApplication application, String previousStatus, String newStatus) {
        String logMessage = String.format("Application #%d status updated from '%s' to '%s'",
                application.getId(), previousStatus, newStatus);

        ActivityLogEntry entry = new ActivityLogEntry(
                application.getId(),
                previousStatus,
                newStatus,
                logMessage,
                LocalDateTime.now()
        );

        activityRegistry.computeIfAbsent(application.getId(), k -> new ArrayList<>()).add(entry);

        System.out.printf("  [ACTIVITY OBSERVER] %s at %s%n", logMessage, entry.timestamp());
    }

    public List<ActivityLogEntry> getActivitiesForApplication(Long applicationId) {
        return activityRegistry.getOrDefault(applicationId, Collections.emptyList());
    }

    public List<ActivityLogEntry> getAllActivities() {
        List<ActivityLogEntry> all = new ArrayList<>();
        activityRegistry.values().forEach(all::addAll);
        return all;
    }
}
