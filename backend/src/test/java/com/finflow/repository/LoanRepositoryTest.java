package com.finflow.repository;

import com.finflow.model.Customer;
import com.finflow.model.LoanApplication;
import com.finflow.model.LoanProduct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class LoanRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LoanProductRepository loanProductRepository;

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Test
    @DisplayName("CustomerRepository - save and find by email")
    void shouldSaveAndFindCustomer() {
        Customer customer = new Customer("John Miller", "john.miller@example.com", new BigDecimal("65000.00"));
        Customer saved = customerRepository.save(customer);

        assertThat(saved.getId()).isNotNull();

        Optional<Customer> found = customerRepository.findByEmail("john.miller@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John Miller");
    }

    @Test
    @DisplayName("LoanProductRepository - save and find by type")
    void shouldSaveAndFindProductByType() {
        LoanProduct product = new LoanProduct("Auto Loan", "VEHICLE",
                new BigDecimal("50000.00"), new BigDecimal("2500000.00"), new BigDecimal("9.50"));
        loanProductRepository.save(product);

        Optional<LoanProduct> found = loanProductRepository.findByLoanTypeIgnoreCase("vehicle");
        assertThat(found).isPresent();
        assertThat(found.get().getProductName()).isEqualTo("Auto Loan");
    }

    @Test
    @DisplayName("LoanApplicationRepository - persist entity with @ManyToOne relationships")
    void shouldPersistApplicationWithRelationships() {
        Customer customer = customerRepository.save(new Customer("Sarah Lee", "sarah.lee@example.com", new BigDecimal("90000.00")));
        LoanProduct product = loanProductRepository.save(new LoanProduct("Executive Loan", "EXECUTIVE",
                new BigDecimal("100000.00"), new BigDecimal("5000000.00"), new BigDecimal("10.00")));

        LoanApplication app = new LoanApplication(
                customer,
                product,
                new BigDecimal("500000.00"),
                36,
                "ELIGIBLE",
                "SUBMITTED",
                "New application"
        );

        LoanApplication savedApp = loanApplicationRepository.save(app);

        assertThat(savedApp.getId()).isNotNull();
        assertThat(savedApp.getCustomer().getName()).isEqualTo("Sarah Lee");
        assertThat(savedApp.getLoanProduct().getLoanType()).isEqualTo("EXECUTIVE");
    }
}
