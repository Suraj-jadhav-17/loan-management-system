package com.loanapp.loan_application.repository.cibil;

import com.loanapp.loan_application.entity.cibil.EligibilityResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EligibilityResultRepo extends JpaRepository<EligibilityResult,Long> {
}
