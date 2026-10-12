package com.loanapp.loan_application.dto.loan;

import com.loanapp.loan_application.entity.loan.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanAccountRequest {
    private Long customerId;
    private Long loanDealId;
    private LoanStatus loanStatus;
    private BigDecimal totalPaidAmount;

}
