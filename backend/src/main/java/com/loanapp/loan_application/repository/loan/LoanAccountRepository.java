package com.loanapp.loan_application.repository.loan;

import com.loanapp.loan_application.entity.loan.LoanAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanAccountRepository extends JpaRepository<LoanAccount, Long> {
    Page<LoanAccount> getLoanAccountByCustomer_CustomerId(Long customerId, Pageable pageable);
    LoanAccount getLoanAccountByLoanDeal_Id(Long loanDealId);

}