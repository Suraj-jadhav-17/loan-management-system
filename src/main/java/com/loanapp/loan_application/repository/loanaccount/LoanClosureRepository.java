package com.loanapp.loan_application.repository.loanaccount;

import com.loanapp.loan_application.entity.loanaccount.LoanClosure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanClosureRepository extends JpaRepository<LoanClosure, Long> {
}