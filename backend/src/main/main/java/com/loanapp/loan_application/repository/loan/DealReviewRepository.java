package com.loanapp.loan_application.repository.loan;

import com.loanapp.loan_application.entity.loan.DealReview;
import com.loanapp.loan_application.entity.loan.DealReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealReviewRepository extends JpaRepository<DealReview, Long> {
    List<DealReview> getDealReviewsByUserId(Long userId);
    List<DealReview> getDealReviewsByStatus( DealReviewStatus status);
}