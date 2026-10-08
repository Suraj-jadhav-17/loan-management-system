package com.loanapp.loan_application.dto.loan;

import com.loanapp.loan_application.entity.User;
import com.loanapp.loan_application.entity.loan.DealReviewStatus;
import com.loanapp.loan_application.entity.loan.LoanDeal;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
