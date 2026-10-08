package com.loanapp.loan_application.serviceimpl.loan;

import com.loanapp.loan_application.dto.loan.DealReviewRequest;
import com.loanapp.loan_application.dto.loan.DealReviewResponse;
import com.loanapp.loan_application.entity.loan.DealReviewStatus;
import com.loanapp.loan_application.service.loan.DealReviewService;

import java.util.List;

public class DealReviewServiceImpl implements DealReviewService {
    @Override
    public DealReviewResponse createReview(Long userId, DealReviewRequest dealReviewRequest) {
        return null;
    }

    @Override
    public DealReviewResponse updateReview(Long userId, Long reviewId, DealReviewRequest dealReviewRequest) {
        return null;
    }

    @Override
    public List<DealReviewResponse> getReviews(Long userId) {
        return List.of();
    }

    @Override
    public List<DealReviewResponse> getReviewsBytStatus(DealReviewStatus dealReviewStatus) {
        return List.of();
    }
}
