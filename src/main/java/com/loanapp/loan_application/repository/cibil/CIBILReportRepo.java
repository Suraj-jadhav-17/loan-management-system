package com.loanapp.loan_application.repository.cibil;

import com.loanapp.loan_application.entity.cibil.CIBILReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CIBILReportRepo extends JpaRepository<CIBILReport, Long> {

    Optional<CIBILReport> findTopByCustomerCustomerIdOrderByCheckDateDesc(Long customerId);

    Page<CIBILReport> findByCustomerCustomerIdOrderByCheckDateDesc(Long customerId, Pageable pageable);
}
