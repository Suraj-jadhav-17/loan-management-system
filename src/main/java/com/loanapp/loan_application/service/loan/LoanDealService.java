package com.loanapp.loan_application.service.loan;


import com.loanapp.loan_application.dto.loan.LoanDealRequestDto;
import com.loanapp.loan_application.dto.loan.LoanDealResponseDto;
import com.loanapp.loan_application.entity.loan.LoanDeal;
import com.loanapp.loan_application.entity.loan.LoanType;

import java.util.List;

public interface LoanDealService {

    LoanDealResponseDto createLoanDeal(LoanDealRequestDto loanDealRequestDto);
    LoanDealResponseDto getLoanDealById(Long id);
    List<LoanDealResponseDto> getLoanDealByCustomerId(Long customerId);
    List<LoanDealResponseDto> getLoanDealByLoanType(LoanType loanType);



}
