package com.loanapp.loan_application.dto.loan;


import com.loanapp.loan_application.entity.loan.DealReviewStatus;
import com.loanapp.loan_application.entity.loan.LoanDeal;
import com.loanapp.loan_application.entity.register.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DealReviewResponse {
    private Long reviewId;

    private LoanDeal loanDeal;  //LoanDeal

    private User officer;  //User

    private DealReviewStatus status;
}
