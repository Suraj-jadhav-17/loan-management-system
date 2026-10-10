package com.loanapp.loan_application.repository.cibil;

import com.loanapp.loan_application.entity.cibil.EligibilityResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EligibilityResultRepo extends JpaRepository<EligibilityResult, Long> {

    Optional<EligibilityResult> findTopByCustomerCustomerIdOrderByIdDesc(Long customerId);

    Page<EligibilityResult> findByCustomerCustomerIdOrderByIdDesc(Long customerId, Pageable pageable);
}
