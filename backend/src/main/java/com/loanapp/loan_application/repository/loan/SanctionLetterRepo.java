package com.loanapp.loan_application.repository.loan;

import com.loanapp.loan_application.entity.loan.SanctionLetter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SanctionLetterRepo extends JpaRepository<SanctionLetter,Long> {
    Optional<SanctionLetter> findByLoanDeal_Id(Long loanDealId);
}
