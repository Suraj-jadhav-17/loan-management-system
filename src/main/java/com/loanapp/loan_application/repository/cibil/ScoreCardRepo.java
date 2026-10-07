package com.loanapp.loan_application.repository.cibil;

import com.loanapp.loan_application.entity.cibil.ScoreCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScoreCardRepo extends JpaRepository<ScoreCard, Long> {

    Optional<ScoreCard> findTopByCustomerCustomerIdOrderByScoreCardIdDesc(Long customerId);
}