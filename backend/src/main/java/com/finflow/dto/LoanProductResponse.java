package com.finflow.dto;

import java.math.BigDecimal;

/**
 * ============================================================================
 * DTO: LoanProductResponse
 * ============================================================================
 * Represents a loan product in the catalog shown to customers.
 */
public class LoanProductResponse {

    private Long id;
    private String productName;
    private String loanType;
    private BigDecimal minimumAmount;
    private BigDecimal maximumAmount;
    private BigDecimal interestRate;
    private String description;

    public LoanProductResponse() {}

    public LoanProductResponse(Long id, String productName, String loanType,
                               BigDecimal minimumAmount, BigDecimal maximumAmount,
                               BigDecimal interestRate, String description) {
        this.id = id;
        this.productName = productName;
        this.loanType = loanType;
        this.minimumAmount = minimumAmount;
        this.maximumAmount = maximumAmount;
        this.interestRate = interestRate;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }

    public BigDecimal getMinimumAmount() { return minimumAmount; }
    public void setMinimumAmount(BigDecimal minimumAmount) { this.minimumAmount = minimumAmount; }

    public BigDecimal getMaximumAmount() { return maximumAmount; }
    public void setMaximumAmount(BigDecimal maximumAmount) { this.maximumAmount = maximumAmount; }

    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
