
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * FINFLOW EDUCATIONAL DESIGN PATTERNS DEMONSTRATION
 * ============================================================================
 * This standalone file demonstrates the 5 Core Design Patterns used in FinFlow
 * without requiring any external libraries or Spring framework dependencies.
 *
 * Patterns demonstrated:
 * 1. Factory Pattern (Creational) - Loan Product Creation
 * 2. Strategy Pattern (Behavioral) - Eligibility Evaluation
 * 3. Observer Pattern (Behavioral) - Activity & Notification Logging
 * 4. Decorator Pattern (Structural) - Execution Time & Logging Wrapper
 * 5. Singleton Pattern (Creational) - Spring Bean Concept & Instance Sharing
 * ============================================================================
 */
public class DesignPatternsDemo {

    public static void main(String[] args) {
        System.out.println("=============================================================");
        System.out.println("  FINFLOW - DESIGN PATTERNS IN ACTION (JAVA STANDALONE DEMO) ");
        System.out.println("=============================================================\n");

        // --------------------------------------------------------------------
        // 1. FACTORY PATTERN IN ACTION
        // --------------------------------------------------------------------
        System.out.println(">>> 1. FACTORY PATTERN: Creating Loan Products polymorphically");
        LoanProductFactory personalFactory = new PersonalLoanFactory();
        LoanProductFactory homeFactory = new HomeLoanFactory();
        LoanProductFactory eduFactory = new EducationLoanFactory();

        LoanProduct personalProduct = personalFactory.createLoanProduct();
        LoanProduct homeProduct = homeFactory.createLoanProduct();
        LoanProduct eduProduct = eduFactory.createLoanProduct();

        System.out.println("Created: " + personalProduct);
        System.out.println("Created: " + homeProduct);
        System.out.println("Created: " + eduProduct);
        System.out.println();

        // --------------------------------------------------------------------
        // 2. STRATEGY PATTERN IN ACTION
        // --------------------------------------------------------------------
        System.out.println(">>> 2. STRATEGY PATTERN: Evaluating applicant with different strategies");
        Applicant applicant = new Applicant("Alice Johnson", new BigDecimal("75000.00"), 740);

        // We can dynamically swap the eligibility strategy at runtime!
        EligibilityStrategy personalStrategy = new PersonalLoanEligibilityStrategy();
        EligibilityStrategy homeStrategy = new HomeLoanEligibilityStrategy();

        EligibilityResult personalResult = personalStrategy.evaluate(applicant, new BigDecimal("300000.00"), 24);
        EligibilityResult homeResult = homeStrategy.evaluate(applicant, new BigDecimal("4500000.00"), 240);

        System.out.println("Personal Loan Evaluation -> " + personalResult);
        System.out.println("Home Loan Evaluation     -> " + homeResult);
        System.out.println();

        // --------------------------------------------------------------------
        // 3. DECORATOR PATTERN IN ACTION
        // --------------------------------------------------------------------
        System.out.println(">>> 3. DECORATOR PATTERN: Adding logging & timing without altering basic evaluator");
        // Core component
        LoanEvaluator basicEvaluator = new BasicLoanEvaluator(homeStrategy);
        // Decorated component (wraps the basic evaluator)
        LoanEvaluator loggingEvaluator = new LoggingLoanEvaluator(basicEvaluator);

        // Execution passes through the decorator transparently!
        EligibilityResult decoratedResult = loggingEvaluator.evaluate(applicant, new BigDecimal("4000000.00"), 180);
        System.out.println("Decorated Result: " + decoratedResult.isEligible() + " | EMI: ₹" + decoratedResult.getCalculatedEmi());
        System.out.println();

        // --------------------------------------------------------------------
        // 4. OBSERVER PATTERN IN ACTION
        // --------------------------------------------------------------------
        System.out.println(">>> 4. OBSERVER PATTERN: Status change triggers independent observers");
        LoanApplicationSubject application = new LoanApplicationSubject(101L, applicant, homeProduct, new BigDecimal("4000000.00"));

        // Register observers
        application.addObserver(new LoanActivityObserver());
        application.addObserver(new LoanNotificationObserver());

        // Trigger state transitions
        System.out.println("--- Transitioning to UNDER_REVIEW ---");
        application.updateStatus("UNDER_REVIEW");

        System.out.println("--- Transitioning to APPROVED ---");
        application.updateStatus("APPROVED");
        System.out.println();

        // --------------------------------------------------------------------
        // 5. SINGLETON PATTERN DEMONSTRATION (SPRING-STYLE)
        // --------------------------------------------------------------------
        System.out.println(">>> 5. SINGLETON PATTERN: Demonstrating shared single instance");
        // In Spring, the IoC Container holds single shared instances of services.
        // Here we simulate how a single registry instance is reused across components.
        LoanServiceRegistry registry1 = LoanServiceRegistry.getInstance();
        LoanServiceRegistry registry2 = LoanServiceRegistry.getInstance();

        System.out.println("Instance 1 Hash: " + System.identityHashCode(registry1));
        System.out.println("Instance 2 Hash: " + System.identityHashCode(registry2));
        System.out.println("Are both instances the same object? -> " + (registry1 == registry2));
        System.out.println("=============================================================");
    }

    // ========================================================================
    // DOMAIN MODELS & VALUE OBJECTS
    // ========================================================================
    public static class Applicant {
        private final String name;
        private final BigDecimal monthlyIncome;
        private final int creditScore;

        public Applicant(String name, BigDecimal monthlyIncome, int creditScore) {
            this.name = name;
            this.monthlyIncome = monthlyIncome;
            this.creditScore = creditScore;
        }

        public String getName() { return name; }
        public BigDecimal getMonthlyIncome() { return monthlyIncome; }
        public int getCreditScore() { return creditScore; }
    }

    public static class LoanProduct {
        private final String productName;
        private final String loanType;
        private final BigDecimal minimumAmount;
        private final BigDecimal maximumAmount;
        private final BigDecimal interestRate;

        public LoanProduct(String productName, String loanType, BigDecimal min, BigDecimal max, BigDecimal rate) {
            this.productName = productName;
            this.loanType = loanType;
            this.minimumAmount = min;
            this.maximumAmount = max;
            this.interestRate = rate;
        }

        public String getProductName() { return productName; }
        public String getLoanType() { return loanType; }
        public BigDecimal getInterestRate() { return interestRate; }

        @Override
        public String toString() {
            return String.format("[%s (%s)] Min: ₹%s, Max: ₹%s, Rate: %s%%",
                    productName, loanType, minimumAmount, maximumAmount, interestRate);
        }
    }

    public static class EligibilityResult {
        private final boolean eligible;
        private final BigDecimal maxEligibleAmount;
        private final BigDecimal calculatedEmi;
        private final String reason;

        public EligibilityResult(boolean eligible, BigDecimal maxEligibleAmount, BigDecimal calculatedEmi, String reason) {
            this.eligible = eligible;
            this.maxEligibleAmount = maxEligibleAmount;
            this.calculatedEmi = calculatedEmi;
            this.reason = reason;
        }

        public boolean isEligible() { return eligible; }
        public BigDecimal getCalculatedEmi() { return calculatedEmi; }

        @Override
        public String toString() {
            return String.format("Eligible: %s | Max Allowed: ₹%s | EMI: ₹%s | Reason: %s",
                    eligible, maxEligibleAmount, calculatedEmi, reason);
        }
    }

    // ========================================================================
    // 1. FACTORY PATTERN IMPLEMENTATION
    // ========================================================================
    public interface LoanProductFactory {
        LoanProduct createLoanProduct();
    }

    public static class PersonalLoanFactory implements LoanProductFactory {
        @Override
        public LoanProduct createLoanProduct() {
            return new LoanProduct("Flexi-Personal Loan", "PERSONAL",
                    new BigDecimal("25000.00"), new BigDecimal("1500000.00"), new BigDecimal("12.50"));
        }
    }

    public static class HomeLoanFactory implements LoanProductFactory {
        @Override
        public LoanProduct createLoanProduct() {
            return new LoanProduct("Dream Home Mortgage", "HOME",
                    new BigDecimal("500000.00"), new BigDecimal("20000000.00"), new BigDecimal("8.45"));
        }
    }

    public static class EducationLoanFactory implements LoanProductFactory {
        @Override
        public LoanProduct createLoanProduct() {
            return new LoanProduct("Global Scholar Education Loan", "EDUCATION",
                    new BigDecimal("100000.00"), new BigDecimal("7500000.00"), new BigDecimal("9.20"));
        }
    }

    // ========================================================================
    // 2. STRATEGY PATTERN IMPLEMENTATION
    // ========================================================================
    public interface EligibilityStrategy {
        EligibilityResult evaluate(Applicant applicant, BigDecimal requestedAmount, int tenureMonths);
    }

    public static class PersonalLoanEligibilityStrategy implements EligibilityStrategy {
        @Override
        public EligibilityResult evaluate(Applicant applicant, BigDecimal requestedAmount, int tenureMonths) {
            // Rule: Min income ₹25,000, Min Credit Score 650. Max EMI <= 40% of income.
            if (applicant.getMonthlyIncome().compareTo(new BigDecimal("25000.00")) < 0) {
                return new EligibilityResult(false, BigDecimal.ZERO, BigDecimal.ZERO, "Monthly income below ₹25,000 threshold");
            }
            if (applicant.getCreditScore() < 650) {
                return new EligibilityResult(false, BigDecimal.ZERO, BigDecimal.ZERO, "Credit score below 650 requirement");
            }
            // Max allowed EMI = 40% of income
            BigDecimal maxEmiAllowed = applicant.getMonthlyIncome().multiply(new BigDecimal("0.40"));
            BigDecimal approxEmi = requestedAmount.divide(BigDecimal.valueOf(tenureMonths), 2, RoundingMode.HALF_EVEN);

            if (approxEmi.compareTo(maxEmiAllowed) > 0) {
                return new EligibilityResult(false, maxEmiAllowed.multiply(BigDecimal.valueOf(tenureMonths)), approxEmi,
                        "Requested EMI ₹" + approxEmi + " exceeds 40% income cap of ₹" + maxEmiAllowed);
            }

            return new EligibilityResult(true, requestedAmount, approxEmi, "Applicant satisfies all personal loan underwriting criteria");
        }
    }

    public static class HomeLoanEligibilityStrategy implements EligibilityStrategy {
        @Override
        public EligibilityResult evaluate(Applicant applicant, BigDecimal requestedAmount, int tenureMonths) {
            // Rule: Min income ₹40,000, Min Credit Score 700. Max EMI <= 50% of income.
            if (applicant.getMonthlyIncome().compareTo(new BigDecimal("40000.00")) < 0) {
                return new EligibilityResult(false, BigDecimal.ZERO, BigDecimal.ZERO, "Monthly income below ₹40,000 threshold for Home Loan");
            }
            if (applicant.getCreditScore() < 700) {
                return new EligibilityResult(false, BigDecimal.ZERO, BigDecimal.ZERO, "Credit score below 700 requirement for Home Loan");
            }
            BigDecimal maxEmiAllowed = applicant.getMonthlyIncome().multiply(new BigDecimal("0.50"));
            BigDecimal approxEmi = requestedAmount.divide(BigDecimal.valueOf(tenureMonths), 2, RoundingMode.HALF_EVEN);

            if (approxEmi.compareTo(maxEmiAllowed) > 0) {
                return new EligibilityResult(false, maxEmiAllowed.multiply(BigDecimal.valueOf(tenureMonths)), approxEmi,
                        "Requested EMI ₹" + approxEmi + " exceeds 50% income cap of ₹" + maxEmiAllowed);
            }

            return new EligibilityResult(true, requestedAmount, approxEmi, "Applicant satisfies all home loan underwriting criteria");
        }
    }

    public static class EducationLoanEligibilityStrategy implements EligibilityStrategy {
        @Override
        public EligibilityResult evaluate(Applicant applicant, BigDecimal requestedAmount, int tenureMonths) {
            // Rule: Min income ₹20,000 (applicant/co-applicant), Min Credit Score 600.
            if (applicant.getCreditScore() < 600) {
                return new EligibilityResult(false, BigDecimal.ZERO, BigDecimal.ZERO, "Credit score below 600 requirement for Education Loan");
            }
            BigDecimal approxEmi = requestedAmount.divide(BigDecimal.valueOf(tenureMonths), 2, RoundingMode.HALF_EVEN);
            return new EligibilityResult(true, requestedAmount, approxEmi, "Education loan eligibility approved with student grace period");
        }
    }

    // ========================================================================
    // 3. DECORATOR PATTERN IMPLEMENTATION
    // ========================================================================
    public interface LoanEvaluator {
        EligibilityResult evaluate(Applicant applicant, BigDecimal requestedAmount, int tenureMonths);
    }

    // Concrete Component
    public static class BasicLoanEvaluator implements LoanEvaluator {
        private final EligibilityStrategy strategy;

        public BasicLoanEvaluator(EligibilityStrategy strategy) {
            this.strategy = strategy;
        }

        @Override
        public EligibilityResult evaluate(Applicant applicant, BigDecimal requestedAmount, int tenureMonths) {
            return strategy.evaluate(applicant, requestedAmount, tenureMonths);
        }
    }

    // Concrete Decorator
    public static class LoggingLoanEvaluator implements LoanEvaluator {
        private final LoanEvaluator wrappedEvaluator;

        public LoggingLoanEvaluator(LoanEvaluator wrappedEvaluator) {
            this.wrappedEvaluator = wrappedEvaluator;
        }

        @Override
        public EligibilityResult evaluate(Applicant applicant, BigDecimal requestedAmount, int tenureMonths) {
            long startTime = System.nanoTime();
            System.out.printf("  [DECORATOR LOG] >> Starting evaluation for applicant '%s' | Requested: ₹%s%n",
                    applicant.getName(), requestedAmount);

            // Delegate to the wrapped object!
            EligibilityResult result = wrappedEvaluator.evaluate(applicant, requestedAmount, tenureMonths);

            long durationNs = System.nanoTime() - startTime;
            double durationMs = durationNs / 1_000_000.0;
            System.out.printf("  [DECORATOR LOG] << Finished evaluation in %.3f ms | Outcome: %s%n",
                    durationMs, result.isEligible() ? "APPROVED" : "REJECTED");

            return result;
        }
    }

    // ========================================================================
    // 4. OBSERVER PATTERN IMPLEMENTATION
    // ========================================================================
    public interface LoanObserver {
        void onStatusChanged(LoanApplicationSubject application, String oldStatus, String newStatus);
    }

    public static class LoanActivityObserver implements LoanObserver {
        @Override
        public void onStatusChanged(LoanApplicationSubject application, String oldStatus, String newStatus) {
            System.out.printf("  [ACTIVITY OBSERVER] Application #%d status changed from '%s' to '%s' at %s%n",
                    application.getId(), oldStatus, newStatus, LocalDateTime.now());
        }
    }

    public static class LoanNotificationObserver implements LoanObserver {
        @Override
        public void onStatusChanged(LoanApplicationSubject application, String oldStatus, String newStatus) {
            System.out.printf("  [NOTIFICATION OBSERVER] Alert for customer '%s': Your loan is now '%s'!%n",
                    application.getApplicant().getName(), newStatus);
        }
    }

    // Subject (Observable)
    public static class LoanApplicationSubject {
        private final Long id;
        private final Applicant applicant;
        private final LoanProduct product;
        private final BigDecimal requestedAmount;
        private String currentStatus;
        private final List<LoanObserver> observers = new ArrayList<>();

        public LoanApplicationSubject(Long id, Applicant applicant, LoanProduct product, BigDecimal requestedAmount) {
            this.id = id;
            this.applicant = applicant;
            this.product = product;
            this.requestedAmount = requestedAmount;
            this.currentStatus = "SUBMITTED";
        }

        public void addObserver(LoanObserver observer) {
            observers.add(observer);
        }

        public void removeObserver(LoanObserver observer) {
            observers.remove(observer);
        }

        public void updateStatus(String newStatus) {
            String oldStatus = this.currentStatus;
            this.currentStatus = newStatus;
            notifyObservers(oldStatus, newStatus);
        }

        private void notifyObservers(String oldStatus, String newStatus) {
            for (LoanObserver observer : observers) {
                observer.onStatusChanged(this, oldStatus, newStatus);
            }
        }

        public Long getId() { return id; }
        public Applicant getApplicant() { return applicant; }
        public String getCurrentStatus() { return currentStatus; }
    }

    // ========================================================================
    // 5. SINGLETON PATTERN IMPLEMENTATION (SIMULATING SPRING BEAN REGISTRY)
    // ========================================================================
    public static class LoanServiceRegistry {
        // Eagerly created or lazily thread-safe instance
        private static final LoanServiceRegistry INSTANCE = new LoanServiceRegistry();

        // Private constructor prevents external instantiation via `new LoanServiceRegistry()`
        private LoanServiceRegistry() {
            System.out.println("  [SINGLETON] Initializing LoanServiceRegistry (Only happens ONCE during startup!)");
        }

        public static LoanServiceRegistry getInstance() {
            return INSTANCE;
        }

        public String getSystemStatus() {
            return "FinFlow Service Engine: HEALTHY";
        }
    }
}
