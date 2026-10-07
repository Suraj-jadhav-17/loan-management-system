package com.loanapp.loan_application.repository.registration;

import com.loanapp.loan_application.entity.registration.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
   boolean existsByEmail(String email);
}
