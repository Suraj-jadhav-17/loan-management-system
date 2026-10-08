package com.loanapp.loan_application.repository.loanaccount;


import com.loanapp.loan_application.entity.foreclosure.LoanClosure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanClosureRepo
        extends JpaRepository<LoanClosure, Long> {

    Optional<LoanClosure> findByLoanAccountLoanAccountId(Long loanAccountId);
}