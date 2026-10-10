package com.loanapp.loan_application.repository.register;

import com.loanapp.loan_application.entity.register.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
   boolean existsByEmail(String email);
}
