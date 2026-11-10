package com.finflow.strategy;

import com.finflow.dto.EligibilityResponse;
import com.finflow.model.Customer;
import com.finflow.model.LoanApplication;
import com.finflow.model.LoanProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EligibilityStrategyTest {

    private PersonalLoanEligibilityStrategy personalStrategy;
    private HomeLoanEligibilityStrategy homeStrategy;
    private EducationLoanEligibilityStrategy educationStrategy;
    private EligibilityStrategyRegistry registry;

    @BeforeEach
    void setUp() {
        personalStrategy = new PersonalLoanEligibilityStrategy();
        homeStrategy = new HomeLoanEligibilityStrategy();
        educationStrategy = new EducationLoanEligibilityStrategy();
        registry = new EligibilityStrategyRegistry(List.of(personalStrategy, homeStrategy, educationStrategy));
    }

    @Test
    @DisplayName("PersonalLoanStrategy - eligible when income and EMI are within caps")
    void personalLoanShouldBeEligible() {
        Customer customer = new Customer("Bob", "bob@example.com", new BigDecimal("50000.00"));
        LoanProduct product = new LoanProduct("Personal", "PERSONAL", new BigDecimal("25000"), new BigDecimal("1000000"), new BigDecimal("12"));
        LoanApplication app = new LoanApplication(customer, product, new BigDecimal("200000.00"), 20, "PENDING", "SUBMITTED", "");

        EligibilityResponse response = personalStrategy.evaluate(customer, app);

        assertThat(response.isEligible()).isTrue();
        assertThat(response.getCalculatedEmi()).isEqualByComparingTo(new BigDecimal("10000.00"));
    }

    @Test
    @DisplayName("PersonalLoanStrategy - ineligible when income is below ₹25,000 threshold")
    void personalLoanShouldFailOnLowIncome() {
        Customer customer = new Customer("Charlie", "charlie@example.com", new BigDecimal("18000.00"));
        LoanProduct product = new LoanProduct("Personal", "PERSONAL", new BigDecimal("25000"), new BigDecimal("1000000"), new BigDecimal("12"));
        LoanApplication app = new LoanApplication(customer, product, new BigDecimal("50000.00"), 12, "PENDING", "SUBMITTED", "");

        EligibilityResponse response = personalStrategy.evaluate(customer, app);

        assertThat(response.isEligible()).isFalse();
        assertThat(response.getDecisionReason()).contains("below the minimum personal loan threshold");
    }

    @Test
    @DisplayName("HomeLoanStrategy - ineligible when EMI exceeds 50% income cap")
    void homeLoanShouldFailOnExcessiveDti() {
        Customer customer = new Customer("David", "david@example.com", new BigDecimal("45000.00"));
        LoanProduct product = new LoanProduct("Home", "HOME", new BigDecimal("500000"), new BigDecimal("10000000"), new BigDecimal("8.5"));
        // Requested 3,000,000 over 60 months = EMI ₹50,000 (exceeds 50% of 45,000 = ₹22,500)
        LoanApplication app = new LoanApplication(customer, product, new BigDecimal("3000000.00"), 60, "PENDING", "SUBMITTED", "");

        EligibilityResponse response = homeStrategy.evaluate(customer, app);

        assertThat(response.isEligible()).isFalse();
        assertThat(response.getDecisionReason()).contains("exceeds the 50% residential debt-to-income cap");
    }

    @Test
    @DisplayName("EducationLoanStrategy - eligible for students with qualifying household income")
    void educationLoanShouldBeEligible() {
        Customer customer = new Customer("Emma", "emma@example.com", new BigDecimal("30000.00"));
        LoanProduct product = new LoanProduct("Education", "EDUCATION", new BigDecimal("100000"), new BigDecimal("5000000"), new BigDecimal("9.0"));
        LoanApplication app = new LoanApplication(customer, product, new BigDecimal("500000.00"), 48, "PENDING", "SUBMITTED", "");

        EligibilityResponse response = educationStrategy.evaluate(customer, app);

        assertThat(response.isEligible()).isTrue();
        assertThat(response.getDecisionReason()).contains("grace period");
    }

    @Test
    @DisplayName("EligibilityStrategyRegistry - throws exception on unrecognized loan type")
    void registryShouldThrowOnUnknownType() {
        assertThatThrownBy(() -> registry.getStrategy("CRYPTO"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No eligibility strategy found");
    }
}
