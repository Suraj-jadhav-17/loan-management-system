package com.loanapp.loan_application.repository.register;

import com.loanapp.loan_application.entity.register.Customer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
   boolean existsByEmail(String email);

    Optional<Customer> findByEmail(String email);
    boolean existsByAadhaarNo(@NotBlank @Size(min = 12, max = 12, message = "Aadhaar number must be exactly 12 digits") String aadhaarNo);

    boolean existsByPanCard(String panCard);

    boolean existsByMobileNo(@NotBlank @Size(min = 10, max = 10, message = "Mobile number must be exactly 10 digits") String mobileNo);

}
