package com.loanapp.loan_application.dto.loan;

import com.loanapp.loan_application.entity.loan.DisbursementStatus;
import com.loanapp.loan_application.entity.loan.LoanDeal;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DisbursementResponse {

    private Long disbursementId;


    private LoanDeal loanDeal;  //LoanDeal


    private BigDecimal disburseAmount;


    private String bankPartner;

    private LocalDateTime disbursementDate;


    private DisbursementStatus status;
}
