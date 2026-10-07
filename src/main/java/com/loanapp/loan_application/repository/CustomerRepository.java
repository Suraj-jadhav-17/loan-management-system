package com.loanapp.loan_application.repository;

import com.loanapp.loan_application.entity.Customer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
   boolean existsByEmail(String email);
}
