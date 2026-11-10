package com.finflow.factory;

import com.finflow.model.LoanProduct;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class HomeLoanFactory implements LoanProductFactory {

    @Override
    public LoanProduct createProduct() {
        return new LoanProduct(
                "Dream Home Mortgage",
                "HOME",
                new BigDecimal("500000.00"),
                new BigDecimal("20000000.00"),
                new BigDecimal("8.45")
        );
    }

    @Override
    public String getSupportedLoanType() {
        return "HOME";
    }
}
