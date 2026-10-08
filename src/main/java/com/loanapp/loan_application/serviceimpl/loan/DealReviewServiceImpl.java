package com.loanapp.loan_application.serviceimpl.loan;

import com.loanapp.loan_application.dto.loan.DealReviewRequest;
import com.loanapp.loan_application.dto.loan.DealReviewResponse;
import com.loanapp.loan_application.entity.loan.DealReviewStatus;
import com.loanapp.loan_application.entity.loan.LoanDeal;
import com.loanapp.loan_application.entity.register.Customer;
import com.loanapp.loan_application.entity.register.User;
import com.loanapp.loan_application.exception.ResourceNotFoundException;
import com.loanapp.loan_application.exception.UnAuthorizationException;
import com.loanapp.loan_application.repository.cibil.ScoreCardRepo;
import com.loanapp.loan_application.repository.loan.DealReviewRepository;
import com.loanapp.loan_application.repository.loan.LoanDealRepo;
import com.loanapp.loan_application.repository.register.UserRepository;
import com.loanapp.loan_application.service.loan.DealReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class DealReviewServiceImpl implements DealReviewService {
    private final DealReviewRepository reviewRepo;
    private final UserRepository userRepo;
    private final LoanDealRepo dealRepo;
    private final ScoreCardRepo cardRepo;
    @Override
    public DealReviewResponse createReview( DealReviewRequest request) {
        User user = userRepo.findById(request.getOfficerId()).orElseThrow(()-> new ResourceNotFoundException("User not found"));
        if(!user.getRole().getRoleName().equals("LoanOfficer")){
            throw new UnAuthorizationException("Only loan_officer can access ");
        }
        LoanDeal  loanDeal = dealRepo.findById(request.getLoanDealId()).orElseThrow(()-> new ResourceNotFoundException("LoanDeal not found"));




        return null;
    }

    @Override
    public DealReviewResponse updateReview( Long reviewId, DealReviewRequest dealReviewRequest) {
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
