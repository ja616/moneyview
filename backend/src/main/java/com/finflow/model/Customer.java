package com.finflow.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/**
 * ============================================================================
 * ENTITY: Customer
 * ============================================================================
 * Maps to MySQL table 'customers'.
 *
 * Annotations explained:
 * - @Entity: Marks this Java POJO as a JPA entity managed by Hibernate.
 * - @Table(name = "customers"): Maps to the specific table name in MySQL.
 * - @Id: Declares the primary key field.
 * - @GeneratedValue(strategy = GenerationType.IDENTITY): Delegates primary key auto-increment to MySQL.
 * - @Column: Configures column constraints (nullable, unique, precision/scale for decimals).
 */
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "monthly_income", nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyIncome;

    // Default no-argument constructor required by Hibernate
    public Customer() {}

    public Customer(String name, String email, BigDecimal monthlyIncome) {
        this.name = name;
        this.email = email;
        this.monthlyIncome = monthlyIncome;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public BigDecimal getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(BigDecimal monthlyIncome) { this.monthlyIncome = monthlyIncome; }
}
