package com.loanapp.loan_application.serviceimpl.loan;

import com.loanapp.loan_application.dto.loan.DealReviewRequest;
import com.loanapp.loan_application.dto.loan.DealReviewResponse;
import com.loanapp.loan_application.entity.loan.DealReview;
import com.loanapp.loan_application.entity.loan.DealReviewStatus;
import com.loanapp.loan_application.entity.loan.LoanDeal;
import com.loanapp.loan_application.entity.loan.SanctionLetter;
import com.loanapp.loan_application.entity.register.Customer;
import com.loanapp.loan_application.entity.register.User;
import com.loanapp.loan_application.exception.InvalidInputException;
import com.loanapp.loan_application.exception.ResourceNotFoundException;
import com.loanapp.loan_application.exception.UnAuthorizationException;
import com.loanapp.loan_application.repository.cibil.ScoreCardRepo;
import com.loanapp.loan_application.repository.loan.DealReviewRepository;
import com.loanapp.loan_application.repository.loan.LoanDealRepo;
import com.loanapp.loan_application.repository.register.UserRepository;
import com.loanapp.loan_application.service.loan.DealReviewService;
import com.loanapp.loan_application.service.loan.SanctionLetterService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class DealReviewServiceImpl implements DealReviewService {
    private final DealReviewRepository reviewRepo;
    private final UserRepository userRepo;
    private final LoanDealRepo dealRepo;
    private final ModelMapper mapper;
    private final SanctionLetterService letterService;

    @Override
    public DealReviewResponse createReview( Long loanId) {


        LoanDeal  loanDeal = dealRepo.findById(loanId).orElseThrow(()-> new ResourceNotFoundException("LoanDeal not found"));

        DealReview review = reviewRepo.save(DealReview.builder()
                .loanDeal(loanDeal)
                .officer( null)
                .status(DealReviewStatus.PENDING)
                .build());


        return mapper.map(review,DealReviewResponse.class) ;
    }

    @Override
    public DealReviewResponse updateReview( Long reviewId, DealReviewRequest request) {
        DealReview review = reviewRepo.findById(reviewId).orElseThrow(()-> new ResourceNotFoundException("Review not found"));
        User user = userRepo.findById(request.getOfficerId()).orElseThrow(()-> new ResourceNotFoundException("User not found"));
        if(!user.getRole().getRoleName().equals("LoanOfficer")){
            throw new UnAuthorizationException("Only loan_officer can change  ");
        }
        if(review.getStatus() != DealReviewStatus.PENDING ){
            throw new InvalidInputException("Review status already Changed");

        }
        review.setStatus(request.getStatus());
        review.setOfficer(user);
        reviewRepo.save(review);
        if(request.getStatus() == DealReviewStatus.APPROVED){
         letterService.createSanctionLetter(review.getReviewId());
        }
        return mapper.map(review,DealReviewResponse.class) ;
    }

    @Override
    public Page<DealReviewResponse> getReviews(Long userId , Integer page, Integer size) {
      return  reviewRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"))).map(review -> mapper.map(review,DealReviewResponse.class)) ;
    }

    @Override
    public Page<DealReviewResponse> getReviewsBytStatus(DealReviewStatus status, Integer page, Integer size) {
        return reviewRepo.getDealReviewsByStatus(status,PageRequest.of(page,size)).map(review -> mapper.map(review,DealReviewResponse.class)) ;
    }
}
