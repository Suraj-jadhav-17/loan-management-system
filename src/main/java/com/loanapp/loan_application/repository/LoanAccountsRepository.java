package com.loanapp.loan_application.repository;

import com.loanapp.loan_application.entity.loanaccount.LoanAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanAccountsRepository extends JpaRepository<LoanAccount, Long> {

}

