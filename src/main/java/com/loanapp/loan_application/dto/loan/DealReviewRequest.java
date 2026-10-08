package com.loanapp.loan_application.dto.loan;


import com.loanapp.loan_application.entity.loan.DealReviewStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DealReviewRequest {


    private Long  loanDealId;  //LoanDeal

    private Long officerId;  //User
    private DealReviewStatus status;
}
