package com.finflow.factory;

import com.finflow.model.LoanProduct;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class EducationLoanFactory implements LoanProductFactory {

    @Override
    public LoanProduct createProduct() {
        return new LoanProduct(
                "Global Scholar Education Loan",
                "EDUCATION",
                new BigDecimal("100000.00"),
                new BigDecimal("7500000.00"),
                new BigDecimal("9.20")
        );
    }

    @Override
    public String getSupportedLoanType() {
        return "EDUCATION";
    }
}
