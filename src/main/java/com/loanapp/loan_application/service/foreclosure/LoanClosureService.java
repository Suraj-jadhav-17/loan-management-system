package com.loanapp.loan_application.service.loanaccount;


import com.loanapp.loan_application.dto.foreclosure.LoanClosureResponseDto;

public interface LoanClosureService {

    LoanClosureResponseDto closeLoanNormally(Long loanAccountId, Long closedBy);

    LoanClosureResponseDto closeLoanByForeclosure(Long loanAccountId, Long closedBy);

    LoanClosureResponseDto getClosure(Long loanAccountId);
}