package com.loanapp.loan_application.service.foreclosure;

import com.loanapp.loan_application.dto.foreclosure.LoanClosureResponseDto;

import java.util.List;

public interface LoanClosureService {

    LoanClosureResponseDto closeLoanNormally(Long loanAccountId, Long closedBy);

    LoanClosureResponseDto closeLoanByForeclosure(Long loanAccountId, Long closedBy);

    LoanClosureResponseDto getClosure(Long loanAccountId);

    List<LoanClosureResponseDto> getClosureHistory();
    List<LoanClosureResponseDto> getCustomerClosureHistory(Long customerId);
}