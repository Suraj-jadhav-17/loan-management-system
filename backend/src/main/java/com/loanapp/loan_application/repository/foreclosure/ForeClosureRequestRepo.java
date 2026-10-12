package com.loanapp.loan_application.repository.foreclosure;

import com.loanapp.loan_application.entity.foreclosure.ForeClosureRequest;
import com.loanapp.loan_application.entity.foreclosure.ForeClosureStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForeClosureRequestRepo extends JpaRepository<ForeClosureRequest, Long> {

    List<ForeClosureRequest> findByLoanAccountLoanAccountId(Long loanAccountId);

    Optional<ForeClosureRequest> findTopByLoanAccountLoanAccountIdOrderByForeClosureIdDesc(Long loanAccountId);

    List<ForeClosureRequest> findByStatus(ForeClosureStatus status);

    boolean existsByLoanAccountLoanAccountIdAndStatus(Long loanAccountId, ForeClosureStatus status);
}