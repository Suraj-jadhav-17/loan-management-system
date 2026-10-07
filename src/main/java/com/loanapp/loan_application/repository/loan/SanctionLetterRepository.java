package com.loanapp.loan_application.repository.loan;

import com.loanapp.loan_application.entity.loan.SanctionLetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SanctionLetterRepository extends JpaRepository<SanctionLetter, Long> {
}