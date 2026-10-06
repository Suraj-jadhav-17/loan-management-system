package com.loanapp.loan_application.repository.loanaccount;

import com.loanapp.loan_application.entity.loanaccount.ForeClosureRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForeClosureRequestRepository extends JpaRepository<ForeClosureRequest, Long> {
}