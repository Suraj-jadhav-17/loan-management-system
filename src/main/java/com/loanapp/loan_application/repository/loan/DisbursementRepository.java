package com.loanapp.loan_application.repository.loan;

import com.loanapp.loan_application.entity.loan.Disbursement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisbursementRepository extends JpaRepository<Disbursement, Long> {
}