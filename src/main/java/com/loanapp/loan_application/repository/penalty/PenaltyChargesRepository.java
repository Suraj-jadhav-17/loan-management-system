package com.loanapp.loan_application.repository.penalty;

import com.loanapp.loan_application.entity.penalty.PenaltyCharges;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PenaltyChargesRepository extends JpaRepository<PenaltyCharges, Long> {
    List<PenaltyCharges> findByLoanAccountId(Long loanAccountId);

    List<PenaltyCharges> findByEmiScheduleId(Long emiScheduleId);
    boolean existsByEmiScheduleId(Long emiScheduleId);
}