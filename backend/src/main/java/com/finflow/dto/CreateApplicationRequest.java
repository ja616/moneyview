package com.finflow.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * ============================================================================
 * DTO: CreateApplicationRequest
 * ============================================================================
 * What is a DTO (Data Transfer Object)?
 * A DTO is a simple container class designed to carry data between processes
 * or over HTTP. It has NO business logic.
 *
 * Why not use the database Entity directly?
 * 1. Security: Prevents mass-assignment attacks (e.g., a customer passing "status: APPROVED").
 * 2. Decoupling: Database schema changes do not break the public API contract.
 * 3. Validation: JSR-380 annotations validate incoming input before it ever touches business services.
 *
 * Python/FastAPI Comparison:
 * This is the direct Java equivalent of a Pydantic BaseModel:
 *   class CreateApplicationRequest(BaseModel):
 *       customerName: str
 *       customerEmail: EmailStr
 *       monthlyIncome: condecimal(gt=0)
 */
public class CreateApplicationRequest {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Customer email must be a valid email format")
    private String customerEmail;

    @NotNull(message = "Monthly income is required")
    @DecimalMin(value = "1000.00", message = "Monthly income must be at least ₹1,000.00")
    private BigDecimal monthlyIncome;

    @NotBlank(message = "Loan type is required (PERSONAL, HOME, EDUCATION)")
    private String loanType;

    @NotNull(message = "Requested loan amount is required")
    @DecimalMin(value = "10000.00", message = "Minimum requested loan amount is ₹10,000.00")
    private BigDecimal requestedAmount;

    @NotNull(message = "Tenure in months is required")
    @Min(value = 6, message = "Minimum tenure is 6 months")
    @Max(value = 360, message = "Maximum tenure is 360 months (30 years)")
    private Integer tenureMonths;

    // Default no-argument constructor required by Jackson for JSON deserialization
    public CreateApplicationRequest() {}

    public CreateApplicationRequest(String customerName, String customerEmail, BigDecimal monthlyIncome,
                                    String loanType, BigDecimal requestedAmount, Integer tenureMonths) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.monthlyIncome = monthlyIncome;
        this.loanType = loanType;
        this.requestedAmount = requestedAmount;
        this.tenureMonths = tenureMonths;
    }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public BigDecimal getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(BigDecimal monthlyIncome) { this.monthlyIncome = monthlyIncome; }

    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }

    public BigDecimal getRequestedAmount() { return requestedAmount; }
    public void setRequestedAmount(BigDecimal requestedAmount) { this.requestedAmount = requestedAmount; }

    public Integer getTenureMonths() { return tenureMonths; }
    public void setTenureMonths(Integer tenureMonths) { this.tenureMonths = tenureMonths; }
}
