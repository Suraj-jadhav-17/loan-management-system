package com.loanapp.loan_application.repository;

import com.loanapp.loan_application.entity.scorecard.ScoreCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScoreCardRepository extends JpaRepository<ScoreCard, Long> {

    Optional<ScoreCard> findTopByCustomerCustomerIdOrderByAppliedDateDesc (Long customerId);
}
