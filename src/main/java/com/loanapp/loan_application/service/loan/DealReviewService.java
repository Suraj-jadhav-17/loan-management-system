package com.loanapp.loan_application.service.loan;

import com.loanapp.loan_application.dto.loan.DealReviewRequest;
import com.loanapp.loan_application.dto.loan.DealReviewResponse;
import com.loanapp.loan_application.entity.loan.DealReviewStatus;

import java.util.List;

public interface DealReviewService {

    DealReviewResponse createReview(Long userId,DealReviewRequest dealReviewRequest);
    DealReviewResponse updateReview(Long userId,Long reviewId ,DealReviewRequest dealReviewRequest);
    List<DealReviewResponse> getReviews(Long userId);
    List<DealReviewResponse> getReviewsBytStatus(DealReviewStatus dealReviewStatus);



}
