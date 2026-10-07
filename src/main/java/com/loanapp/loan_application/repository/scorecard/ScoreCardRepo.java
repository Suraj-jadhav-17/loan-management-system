package com.loanapp.loan_application.repository.scorecard;

import com.loanapp.loan_application.entity.scorecard.ScoreCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreCardRepo extends JpaRepository<ScoreCard,Long> {
}
