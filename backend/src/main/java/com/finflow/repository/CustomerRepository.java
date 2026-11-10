package com.finflow.repository;

import com.finflow.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ============================================================================
 * REPOSITORY: CustomerRepository
 * ============================================================================
 * How Spring Data JPA works:
 * By extending JpaRepository<Customer, Long>, Spring Data JPA automatically
 * provides out-of-the-box CRUD methods:
 * - save(customer)
 * - findById(id)
 * - findAll()
 * - deleteById(id)
 *
 * Query Method Derivation:
 * Methods like findByEmail(String email) are parsed by Spring Data at startup.
 * It automatically derives the SQL:
 *   SELECT * FROM customers WHERE email = ?
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
}
