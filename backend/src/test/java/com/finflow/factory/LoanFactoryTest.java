package com.finflow.factory;

import com.finflow.model.LoanProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoanFactoryTest {

    private LoanProductFactoryRegistry registry;
    private PersonalLoanFactory personalFactory;
    private HomeLoanFactory homeFactory;
    private EducationLoanFactory educationFactory;

    @BeforeEach
    void setUp() {
        personalFactory = new PersonalLoanFactory();
        homeFactory = new HomeLoanFactory();
        educationFactory = new EducationLoanFactory();
        registry = new LoanProductFactoryRegistry(List.of(personalFactory, homeFactory, educationFactory));
    }

    @Test
    @DisplayName("PersonalLoanFactory - creates Personal Loan with correct parameters")
    void shouldCreatePersonalLoanProduct() {
        LoanProduct product = personalFactory.createProduct();

        assertThat(product.getLoanType()).isEqualTo("PERSONAL");
        assertThat(product.getProductName()).isEqualTo("Flexi-Personal Loan");
        assertThat(product.getMinimumAmount()).isEqualByComparingTo(new BigDecimal("25000.00"));
        assertThat(product.getMaximumAmount()).isEqualByComparingTo(new BigDecimal("1500000.00"));
        assertThat(product.getInterestRate()).isEqualByComparingTo(new BigDecimal("12.50"));
    }

    @Test
    @DisplayName("HomeLoanFactory - creates Home Mortgage product")
    void shouldCreateHomeLoanProduct() {
        LoanProduct product = homeFactory.createProduct();

        assertThat(product.getLoanType()).isEqualTo("HOME");
        assertThat(product.getProductName()).isEqualTo("Dream Home Mortgage");
        assertThat(product.getMinimumAmount()).isEqualByComparingTo(new BigDecimal("500000.00"));
        assertThat(product.getInterestRate()).isEqualByComparingTo(new BigDecimal("8.45"));
    }

    @Test
    @DisplayName("EducationLoanFactory - creates Education Loan product")
    void shouldCreateEducationLoanProduct() {
        LoanProduct product = educationFactory.createProduct();

        assertThat(product.getLoanType()).isEqualTo("EDUCATION");
        assertThat(product.getProductName()).isEqualTo("Global Scholar Education Loan");
        assertThat(product.getInterestRate()).isEqualByComparingTo(new BigDecimal("9.20"));
    }

    @Test
    @DisplayName("LoanProductFactoryRegistry - resolves product creation dynamically")
    void shouldResolveProductViaRegistry() {
        LoanProduct product = registry.createProduct("home");
        assertThat(product.getLoanType()).isEqualTo("HOME");
    }

    @Test
    @DisplayName("LoanProductFactoryRegistry - throws exception on unsupported product")
    void shouldThrowOnUnsupportedType() {
        assertThatThrownBy(() -> registry.createProduct("AIRCRAFT"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No factory registered");
    }
}
