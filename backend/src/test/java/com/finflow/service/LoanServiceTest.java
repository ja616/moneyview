package com.finflow.service;

import com.finflow.decorator.LoanEvaluator;
import com.finflow.dto.CreateApplicationRequest;
import com.finflow.dto.EligibilityResponse;
import com.finflow.dto.LoanApplicationResponse;
import com.finflow.exception.BusinessException;
import com.finflow.exception.ResourceNotFoundException;
import com.finflow.factory.EducationLoanFactory;
import com.finflow.factory.HomeLoanFactory;
import com.finflow.factory.LoanProductFactoryRegistry;
import com.finflow.factory.PersonalLoanFactory;
import com.finflow.model.Customer;
import com.finflow.model.LoanApplication;
import com.finflow.model.LoanProduct;
import com.finflow.observer.LoanActivityObserver;
import com.finflow.observer.LoanApplicationSubject;
import com.finflow.observer.LoanNotificationObserver;
import com.finflow.repository.CustomerRepository;
import com.finflow.repository.LoanApplicationRepository;
import com.finflow.repository.LoanProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoanProductRepository loanProductRepository;

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private LoanEvaluator loanEvaluator;

    private LoanProductFactoryRegistry factoryRegistry;
    private LoanActivityObserver activityObserver;
    private LoanNotificationObserver notificationObserver;
    private LoanApplicationSubject applicationSubject;
    private LoanService loanService;

    private Customer sampleCustomer;
    private LoanProduct sampleProduct;
    private LoanApplication sampleApplication;

    @BeforeEach
    void setUp() {
        // Use real instances for fast, pure domain registries and observers (avoiding bytecode agent reflection issues)
        factoryRegistry = new LoanProductFactoryRegistry(List.of(
                new PersonalLoanFactory(),
                new HomeLoanFactory(),
                new EducationLoanFactory()
        ));
        activityObserver = new LoanActivityObserver();
        notificationObserver = new LoanNotificationObserver();
        applicationSubject = new LoanApplicationSubject(List.of(activityObserver, notificationObserver));

        loanService = new LoanService(
                customerRepository,
                loanProductRepository,
                loanApplicationRepository,
                factoryRegistry,
                loanEvaluator,
                applicationSubject,
                activityObserver,
                notificationObserver
        );

        sampleCustomer = new Customer("Alice Walker", "alice@example.com", new BigDecimal("60000.00"));
        sampleCustomer.setId(1L);

        sampleProduct = new LoanProduct("Flexi-Personal", "PERSONAL", new BigDecimal("25000"), new BigDecimal("1000000"), new BigDecimal("12"));
        sampleProduct.setId(10L);

        sampleApplication = new LoanApplication(sampleCustomer, sampleProduct, new BigDecimal("100000.00"), 12, "PENDING_EVALUATION", "SUBMITTED", "Notes");
        sampleApplication.setId(100L);
    }

    @Test
    @DisplayName("createApplication - successfully creates application and notifies observers")
    void shouldCreateApplicationSuccessfully() {
        CreateApplicationRequest request = new CreateApplicationRequest(
                "Alice Walker", "alice@example.com", new BigDecimal("60000.00"),
                "PERSONAL", new BigDecimal("100000.00"), 12
        );

        when(customerRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(sampleCustomer));
        when(loanProductRepository.findByLoanTypeIgnoreCase("PERSONAL")).thenReturn(Optional.of(sampleProduct));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(sampleApplication);

        LoanApplicationResponse response = loanService.createApplication(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getCustomerName()).isEqualTo("Alice Walker");

        // Verify observer recorded the activity
        assertThat(activityObserver.getActivitiesForApplication(100L)).hasSize(1);
        assertThat(activityObserver.getActivitiesForApplication(100L).get(0).toStatus()).isEqualTo("SUBMITTED");
    }

    @Test
    @DisplayName("createApplication - throws BusinessException when loan type is unsupported")
    void shouldThrowWhenLoanTypeIsUnsupported() {
        CreateApplicationRequest request = new CreateApplicationRequest(
                "Alice Walker", "alice@example.com", new BigDecimal("60000.00"),
                "INVALID_TYPE", new BigDecimal("100000.00"), 12
        );

        assertThatThrownBy(() -> loanService.createApplication(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Unsupported loan type");
    }

    @Test
    @DisplayName("getApplicationById - throws ResourceNotFoundException on invalid ID")
    void shouldThrowWhenApplicationNotFound() {
        when(loanApplicationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.getApplicationById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Loan application not found with ID: 999");
    }

    @Test
    @DisplayName("evaluateApplication - delegates to LoanEvaluator and updates eligibility")
    void shouldEvaluateApplicationSuccessfully() {
        when(loanApplicationRepository.findById(100L)).thenReturn(Optional.of(sampleApplication));

        EligibilityResponse evalResponse = new EligibilityResponse(
                100L, true, new BigDecimal("100000.00"), new BigDecimal("8333.33"), "Approved by evaluator"
        );
        when(loanEvaluator.evaluate(sampleApplication)).thenReturn(evalResponse);
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(sampleApplication);

        EligibilityResponse result = loanService.evaluateApplication(100L);

        assertThat(result.isEligible()).isTrue();
        assertThat(sampleApplication.getEligibilityStatus()).isEqualTo("ELIGIBLE");
        assertThat(activityObserver.getActivitiesForApplication(100L)).hasSize(1);
    }

    @Test
    @DisplayName("approveApplication - updates status to APPROVED and notifies observers")
    void shouldApproveApplicationSuccessfully() {
        when(loanApplicationRepository.findById(100L)).thenReturn(Optional.of(sampleApplication));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(sampleApplication);

        LoanApplicationResponse response = loanService.approveApplication(100L);

        assertThat(response.getApplicationStatus()).isEqualTo("APPROVED");
        assertThat(activityObserver.getActivitiesForApplication(100L)).hasSize(1);
        assertThat(activityObserver.getActivitiesForApplication(100L).get(0).toStatus()).isEqualTo("APPROVED");
    }

    @Test
    @DisplayName("approveApplication - throws BusinessException if application is already REJECTED")
    void shouldNotApproveRejectedApplication() {
        sampleApplication.setApplicationStatus("REJECTED");
        when(loanApplicationRepository.findById(100L)).thenReturn(Optional.of(sampleApplication));

        assertThatThrownBy(() -> loanService.approveApplication(100L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot approve an application that is already REJECTED");
    }

    @Test
    @DisplayName("rejectApplication - updates status to REJECTED and records reason")
    void shouldRejectApplicationSuccessfully() {
        when(loanApplicationRepository.findById(100L)).thenReturn(Optional.of(sampleApplication));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(sampleApplication);

        LoanApplicationResponse response = loanService.rejectApplication(100L, "High debt burden");

        assertThat(response.getApplicationStatus()).isEqualTo("REJECTED");
        assertThat(activityObserver.getActivitiesForApplication(100L)).hasSize(1);
        assertThat(activityObserver.getActivitiesForApplication(100L).get(0).toStatus()).isEqualTo("REJECTED");
    }

    @Test
    @DisplayName("rejectApplication - throws BusinessException if application is already APPROVED")
    void shouldNotRejectApprovedApplication() {
        sampleApplication.setApplicationStatus("APPROVED");
        when(loanApplicationRepository.findById(100L)).thenReturn(Optional.of(sampleApplication));

        assertThatThrownBy(() -> loanService.rejectApplication(100L, "Too late"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot reject an application that is already APPROVED");
    }
}
