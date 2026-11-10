package com.finflow.service;

import com.finflow.decorator.LoanEvaluator;
import com.finflow.dto.CreateApplicationRequest;
import com.finflow.dto.EligibilityResponse;
import com.finflow.dto.LoanApplicationResponse;
import com.finflow.dto.LoanProductResponse;
import com.finflow.exception.BusinessException;
import com.finflow.exception.ResourceNotFoundException;
import com.finflow.factory.LoanProductFactoryRegistry;
import com.finflow.model.Customer;
import com.finflow.model.LoanApplication;
import com.finflow.model.LoanProduct;
import com.finflow.observer.LoanActivityObserver;
import com.finflow.observer.LoanApplicationSubject;
import com.finflow.observer.LoanNotificationObserver;
import com.finflow.repository.CustomerRepository;
import com.finflow.repository.LoanApplicationRepository;
import com.finflow.repository.LoanProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================================
 * SERVICE: LoanService (Full Design Pattern Integration)
 * ============================================================================
 * Coordinates the 5 Core Design Patterns:
 * 1. Factory Pattern: Used via LoanProductFactoryRegistry for catalog creation.
 * 2. Strategy Pattern: Used via EligibilityStrategy implementations for underwriting.
 * 3. Decorator Pattern: Used via LoggingLoanEvaluator wrapping BasicLoanEvaluator.
 * 4. Observer Pattern: Used via LoanApplicationSubject broadcasting to observers.
 * 5. Singleton Pattern: Spring-managed singleton service bean with shared state.
 */
@Service
public class LoanService {

    private final CustomerRepository customerRepository;
    private final LoanProductRepository loanProductRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanProductFactoryRegistry factoryRegistry;
    private final LoanEvaluator loanEvaluator; // Injected as LoggingLoanEvaluator (@Primary decorator)
    private final LoanApplicationSubject applicationSubject;
    private final LoanActivityObserver activityObserver;
    private final LoanNotificationObserver notificationObserver;

    // Constructor-based Dependency Injection
    public LoanService(CustomerRepository customerRepository,
                       LoanProductRepository loanProductRepository,
                       LoanApplicationRepository loanApplicationRepository,
                       LoanProductFactoryRegistry factoryRegistry,
                       LoanEvaluator loanEvaluator,
                       LoanApplicationSubject applicationSubject,
                       LoanActivityObserver activityObserver,
                       LoanNotificationObserver notificationObserver) {
        this.customerRepository = customerRepository;
        this.loanProductRepository = loanProductRepository;
        this.loanApplicationRepository = loanApplicationRepository;
        this.factoryRegistry = factoryRegistry;
        this.loanEvaluator = loanEvaluator;
        this.applicationSubject = applicationSubject;
        this.activityObserver = activityObserver;
        this.notificationObserver = notificationObserver;

        System.out.printf("  [SPRING IoC] LoanService Singleton Initialized with Design Patterns (Hash: %d)%n",
                System.identityHashCode(this));
    }

    /**
     * Seeds catalog using the Factory Pattern!
     */
    @PostConstruct
    @Transactional
    public void seedDefaultCatalog() {
        if (loanProductRepository.count() == 0) {
            factoryRegistry.getAllFactories().forEach(factory -> {
                LoanProduct product = factory.createProduct();
                loanProductRepository.save(product);
            });
            System.out.println("  [FACTORY PATTERN] Seeded default loan products using LoanProductFactoryRegistry.");
        }
    }

    @Transactional(readOnly = true)
    public List<LoanProductResponse> getAllProducts() {
        return loanProductRepository.findAll().stream()
                .map(this::mapProductToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public LoanApplicationResponse createApplication(CreateApplicationRequest request) {
        // Verify supported loan type via Factory Registry
        if (!factoryRegistry.supports(request.getLoanType())) {
            throw new BusinessException("Unsupported loan type: " + request.getLoanType()
                    + ". Supported types: PERSONAL, HOME, EDUCATION");
        }

        // 1. Fetch or create Customer
        Customer customer = customerRepository.findByEmail(request.getCustomerEmail())
                .orElseGet(() -> customerRepository.save(new Customer(
                        request.getCustomerName(),
                        request.getCustomerEmail(),
                        request.getMonthlyIncome()
                )));

        if (request.getMonthlyIncome().compareTo(customer.getMonthlyIncome()) != 0) {
            customer.setMonthlyIncome(request.getMonthlyIncome());
            customerRepository.save(customer);
        }

        // 2. Fetch LoanProduct
        LoanProduct product = loanProductRepository.findByLoanTypeIgnoreCase(request.getLoanType())
                .orElseThrow(() -> new BusinessException("Product not found for type: " + request.getLoanType()));

        // 3. Assemble and persist LoanApplication
        LoanApplication application = new LoanApplication(
                customer,
                product,
                request.getRequestedAmount(),
                request.getTenureMonths(),
                "PENDING_EVALUATION",
                "SUBMITTED",
                "Application created and queued for underwriting."
        );

        LoanApplication saved = loanApplicationRepository.save(application);

        // 4. Observer Pattern: Broadcast status change from NEW -> SUBMITTED
        applicationSubject.notifyStatusChange(saved, "NEW", "SUBMITTED");

        return mapApplicationToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<LoanApplicationResponse> getAllApplications() {
        return loanApplicationRepository.findAll().stream()
                .map(this::mapApplicationToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LoanApplicationResponse getApplicationById(Long id) {
        LoanApplication application = loanApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan application not found with ID: " + id));
        return mapApplicationToResponse(application);
    }

    /**
     * Evaluates application using Decorator Pattern (LoggingLoanEvaluator)
     * which delegates to Strategy Pattern (EligibilityStrategy).
     */
    @Transactional
    public EligibilityResponse evaluateApplication(Long id) {
        LoanApplication application = loanApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan application not found with ID: " + id));

        // Runs through the Decorator (timing & logging) and Strategy (underwriting math)
        EligibilityResponse response = loanEvaluator.evaluate(application);

        String previousStatus = application.getEligibilityStatus();
        application.setEligibilityStatus(response.isEligible() ? "ELIGIBLE" : "INELIGIBLE");
        application.setNotes("Evaluated: " + response.getDecisionReason());
        loanApplicationRepository.save(application);

        // Observer Pattern: Notify evaluation outcome
        applicationSubject.notifyStatusChange(application, previousStatus, application.getEligibilityStatus());

        return response;
    }

    @Transactional
    public LoanApplicationResponse approveApplication(Long id) {
        LoanApplication application = loanApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan application not found with ID: " + id));

        if ("REJECTED".equalsIgnoreCase(application.getApplicationStatus())) {
            throw new BusinessException("Cannot approve an application that is already REJECTED.");
        }

        String oldStatus = application.getApplicationStatus();
        application.setApplicationStatus("APPROVED");
        application.setNotes("Application approved by Administrator on " + LocalDateTime.now());
        LoanApplication saved = loanApplicationRepository.save(application);

        // Observer Pattern: Broadcast APPROVED status change
        applicationSubject.notifyStatusChange(saved, oldStatus, "APPROVED");

        return mapApplicationToResponse(saved);
    }

    @Transactional
    public LoanApplicationResponse rejectApplication(Long id, String reason) {
        LoanApplication application = loanApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan application not found with ID: " + id));

        if ("APPROVED".equalsIgnoreCase(application.getApplicationStatus())) {
            throw new BusinessException("Cannot reject an application that is already APPROVED.");
        }

        String oldStatus = application.getApplicationStatus();
        application.setApplicationStatus("REJECTED");
        application.setNotes("Application rejected: " + (reason != null ? reason : "Administrative decision."));
        LoanApplication saved = loanApplicationRepository.save(application);

        // Observer Pattern: Broadcast REJECTED status change
        applicationSubject.notifyStatusChange(saved, oldStatus, "REJECTED");

        return mapApplicationToResponse(saved);
    }

    // Diagnostic and Observer Read Endpoints
    public List<LoanActivityObserver.ActivityLogEntry> getActivities(Long applicationId) {
        return activityObserver.getActivitiesForApplication(applicationId);
    }

    public List<LoanNotificationObserver.NotificationEntry> getNotifications(Long applicationId) {
        return notificationObserver.getNotificationsForApplication(applicationId);
    }

    public int getBeanIdentityHashCode() {
        return System.identityHashCode(this);
    }

    private LoanApplicationResponse mapApplicationToResponse(LoanApplication app) {
        return new LoanApplicationResponse(
                app.getId(),
                app.getCustomer().getName(),
                app.getCustomer().getEmail(),
                app.getLoanProduct().getLoanType(),
                app.getRequestedAmount(),
                app.getTenureMonths(),
                app.getEligibilityStatus(),
                app.getApplicationStatus(),
                app.getNotes(),
                app.getCreatedAt()
        );
    }

    private LoanProductResponse mapProductToResponse(LoanProduct product) {
        return new LoanProductResponse(
                product.getId(),
                product.getProductName(),
                product.getLoanType(),
                product.getMinimumAmount(),
                product.getMaximumAmount(),
                product.getInterestRate(),
                "Catalog product for " + product.getLoanType() + " financing"
        );
    }
}
