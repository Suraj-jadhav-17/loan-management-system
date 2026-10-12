package com.loanapp.loan_application.dto.loan;


import com.loanapp.loan_application.entity.loan.LoanType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanDealRequestDto {

    private Long customerId;

    private LoanType loanType;

    private BigDecimal amount;



    private Long tenureMonths;

    private BigDecimal emiAmount;

    private String bankName;
    private String bankAccountNumber;
    private String ifscCode;
    private Long emiDay;

}
