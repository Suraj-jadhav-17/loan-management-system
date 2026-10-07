package com.loanapp.loan_application.repository.loanaccount;

import com.loanapp.loan_application.entity.loanaccount.LoanAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanAccountRepository extends JpaRepository<LoanAccount, Long> {
}