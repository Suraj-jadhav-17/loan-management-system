package com.loanapp.loan_application.dto.loan;


import com.loanapp.loan_application.entity.loan.LoanType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanDealRequestDto {

    private Long customerId;

    private LoanType loanType;

    private  Double amount;



    private Integer tenureMonths;

    private Double emiAmount;

    private String bankName;
    private String bankAccountNumber;
    private String ifscCode;
    private Integer emiDay;
    private Double approvedAmount;
}
