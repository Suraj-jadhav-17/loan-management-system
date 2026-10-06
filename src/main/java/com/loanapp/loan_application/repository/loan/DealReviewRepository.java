package com.loanapp.loan_application.repository.loan;

import com.loanapp.loan_application.entity.loan.DealReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DealReviewRepository extends JpaRepository<DealReview, Long> {
}