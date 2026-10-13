package com.loanapp.loan_application.repository.payment;

import com.loanapp.loan_application.entity.payment.LoanPayments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanPaymentsRepository extends JpaRepository<LoanPayments, Long> {
    List<LoanPayments> findByLoanAccountId(Long loanAccountId);
}
