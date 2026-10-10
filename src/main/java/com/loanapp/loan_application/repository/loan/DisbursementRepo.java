package com.loanapp.loan_application.repository.loan;

import com.loanapp.loan_application.entity.loan.Disbursement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisbursementRepo extends JpaRepository<Disbursement,Integer> {
}
