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
 * CONCRETE OBSERVER: LoanNotificationObserver
 * ============================================================================
 * Generates customer-facing alerts whenever their application status changes.
 */
@Component
public class LoanNotificationObserver implements LoanObserver {

    public record NotificationEntry(Long applicationId, String customerEmail, String title, String body, LocalDateTime timestamp) {}

    private final Map<Long, List<NotificationEntry>> notificationRegistry = new ConcurrentHashMap<>();

    @Override
    public void onStatusChanged(LoanApplication application, String previousStatus, String newStatus) {
        String title = "Loan Status Update: " + newStatus;
        String body = String.format("Dear %s, your %s application #%d is now '%s'.",
                application.getCustomer().getName(),
                application.getLoanProduct().getProductName(),
                application.getId(),
                newStatus);

        NotificationEntry entry = new NotificationEntry(
                application.getId(),
                application.getCustomer().getEmail(),
                title,
                body,
                LocalDateTime.now()
        );

        notificationRegistry.computeIfAbsent(application.getId(), k -> new ArrayList<>()).add(entry);

        System.out.printf("  [NOTIFICATION OBSERVER] Alert to '%s' [%s]: %s%n",
                entry.customerEmail(), entry.title(), entry.body());
    }

    public List<NotificationEntry> getNotificationsForApplication(Long applicationId) {
        return notificationRegistry.getOrDefault(applicationId, Collections.emptyList());
    }
}
