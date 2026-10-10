package com.loanapp.loan_application.service.loan;

import com.loanapp.loan_application.dto.loan.DealReviewRequest;
import com.loanapp.loan_application.dto.loan.DealReviewResponse;
import com.loanapp.loan_application.entity.loan.DealReviewStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DealReviewService {

    DealReviewResponse createReview(Long dealId);
    DealReviewResponse updateReview(Long reviewId ,DealReviewRequest dealReviewRequest);
    Page<DealReviewResponse> getReviews(Long userId, Integer page, Integer size);
    Page<DealReviewResponse> getReviewsBytStatus(DealReviewStatus dealReviewStatus, Integer page, Integer size);



}
