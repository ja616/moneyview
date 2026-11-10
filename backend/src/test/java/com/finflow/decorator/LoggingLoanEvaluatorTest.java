package com.finflow.decorator;

import com.finflow.dto.EligibilityResponse;
import com.finflow.model.Customer;
import com.finflow.model.LoanApplication;
import com.finflow.model.LoanProduct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoggingLoanEvaluatorTest {

    @Mock
    private LoanEvaluator mockDelegate;

    @Test
    @DisplayName("LoggingLoanEvaluator - delegates execution and preserves result")
    void shouldDelegateAndPreserveResponse() {
        Customer customer = new Customer("Alan Turing", "alan@example.com", new BigDecimal("85000.00"));
        LoanProduct product = new LoanProduct("Home", "HOME", new BigDecimal("500000"), new BigDecimal("10000000"), new BigDecimal("8.5"));
        LoanApplication app = new LoanApplication(customer, product, new BigDecimal("2500000.00"), 120, "PENDING", "SUBMITTED", "");
        app.setId(42L);

        EligibilityResponse expectedResponse = new EligibilityResponse(
                42L, true, new BigDecimal("2500000.00"), new BigDecimal("20833.33"), "Approved by mock delegate"
        );

        when(mockDelegate.evaluate(app)).thenReturn(expectedResponse);

        // Instantiate decorator wrapping mock
        LoggingLoanEvaluator loggingEvaluator = new LoggingLoanEvaluator(mockDelegate);

        EligibilityResponse actualResponse = loggingEvaluator.evaluate(app);

        // Verify delegation occurred exactly once
        verify(mockDelegate, times(1)).evaluate(app);

        // Verify the response is preserved completely
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.isEligible()).isTrue();
        assertThat(actualResponse.getDecisionReason()).isEqualTo("Approved by mock delegate");
        assertThat(actualResponse.getApplicationId()).isEqualTo(42L);
    }
}
