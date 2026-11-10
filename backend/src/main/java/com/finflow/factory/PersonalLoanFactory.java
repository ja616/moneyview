package com.finflow.factory;

import com.finflow.model.LoanProduct;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PersonalLoanFactory implements LoanProductFactory {

    @Override
    public LoanProduct createProduct() {
        return new LoanProduct(
                "Flexi-Personal Loan",
                "PERSONAL",
                new BigDecimal("25000.00"),
                new BigDecimal("1500000.00"),
                new BigDecimal("12.50")
        );
    }

    @Override
    public String getSupportedLoanType() {
        return "PERSONAL";
    }
}
