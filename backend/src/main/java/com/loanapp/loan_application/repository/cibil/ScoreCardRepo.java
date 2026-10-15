package com.loanapp.loan_application.repository.cibil;

import com.loanapp.loan_application.entity.cibil.RiskCategory;
import com.loanapp.loan_application.entity.cibil.ScoreCard;
import com.loanapp.loan_application.entity.cibil.ScoreCardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScoreCardRepo extends JpaRepository<ScoreCard, Long> {

    Optional<ScoreCard> findTopByCustomerCustomerIdOrderByScoreCardIdDesc(Long customerId);

    Page<ScoreCard> findByCustomerCustomerIdOrderByScoreCardIdDesc(Long customerId, Pageable pageable);

 
  

    long countByCurrentStatus(ScoreCardStatus scoreCardStatus);

    long countByRiskCategoryAndCurrentStatus(RiskCategory riskCategory, ScoreCardStatus scoreCardStatus);

    long countByRiskCategory(RiskCategory riskCategory);
}
