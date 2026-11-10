package com.finflow.observer;

import com.finflow.model.Customer;
import com.finflow.model.LoanApplication;
import com.finflow.model.LoanProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LoanObserverTest {

    private LoanActivityObserver activityObserver;
    private LoanNotificationObserver notificationObserver;
    private LoanApplicationSubject subject;
    private LoanApplication testApplication;

    @BeforeEach
    void setUp() {
        activityObserver = new LoanActivityObserver();
        notificationObserver = new LoanNotificationObserver();
        subject = new LoanApplicationSubject(List.of(activityObserver, notificationObserver));

        Customer customer = new Customer("Grace Hopper", "grace@example.com", new BigDecimal("70000.00"));
        LoanProduct product = new LoanProduct("Personal", "PERSONAL", new BigDecimal("25000"), new BigDecimal("1000000"), new BigDecimal("12"));
        testApplication = new LoanApplication(customer, product, new BigDecimal("150000.00"), 12, "PENDING", "SUBMITTED", "");
        testApplication.setId(99L);
    }

    @Test
    @DisplayName("LoanApplicationSubject - notifies both activity and notification observers")
    void shouldNotifyAllObserversOnStatusChange() {
        subject.notifyStatusChange(testApplication, "SUBMITTED", "APPROVED");

        // Verify activity observer recorded log
        List<LoanActivityObserver.ActivityLogEntry> activities = activityObserver.getActivitiesForApplication(99L);
        assertThat(activities).hasSize(1);
        assertThat(activities.get(0).fromStatus()).isEqualTo("SUBMITTED");
        assertThat(activities.get(0).toStatus()).isEqualTo("APPROVED");

        // Verify notification observer recorded alert
        List<LoanNotificationObserver.NotificationEntry> notifications = notificationObserver.getNotificationsForApplication(99L);
        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).customerEmail()).isEqualTo("grace@example.com");
        assertThat(notifications.get(0).title()).contains("APPROVED");
    }

    @Test
    @DisplayName("LoanObserver - multiple transitions accumulate in chronological order")
    void shouldAccumulateHistoryAcrossTransitions() {
        subject.notifyStatusChange(testApplication, "NEW", "SUBMITTED");
        subject.notifyStatusChange(testApplication, "SUBMITTED", "UNDER_REVIEW");
        subject.notifyStatusChange(testApplication, "UNDER_REVIEW", "APPROVED");

        List<LoanActivityObserver.ActivityLogEntry> activities = activityObserver.getActivitiesForApplication(99L);
        assertThat(activities).hasSize(3);
        assertThat(activities.get(0).toStatus()).isEqualTo("SUBMITTED");
        assertThat(activities.get(1).toStatus()).isEqualTo("UNDER_REVIEW");
        assertThat(activities.get(2).toStatus()).isEqualTo("APPROVED");
    }
}
