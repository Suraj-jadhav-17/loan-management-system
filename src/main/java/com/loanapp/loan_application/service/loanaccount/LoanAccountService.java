package com.loanapp.loan_application.service.loanaccount;



import com.loanapp.loan_application.dto.request.loanaccount.LoanAccountRequestDto;
import com.loanapp.loan_application.dto.response.loanaccount.LoanAccountResponseDto;


public interface LoanAccountService {

    LoanAccountResponseDto createLoanAccount(LoanAccountRequestDto requestDto);

    LoanAccountResponseDto getLoanAccountById(Long loanAccountId);
}