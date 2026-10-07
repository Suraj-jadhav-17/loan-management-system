package com.loanapp.loan_application.repository.loan;

import com.loanapp.loan_application.entity.loan.LoanDeal;
import com.loanapp.loan_application.entity.loan.LoanType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanDealRepo extends JpaRepository<LoanDeal, Long> {
    List<LoanDeal> getLoanDealByLoanType(LoanType loanType);
    List<LoanDeal> getLoanDealByCustomer_customerId(Long customerId);
}
