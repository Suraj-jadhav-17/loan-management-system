package com.loanapp.loan_application.service.cibil;

import com.loanapp.loan_application.dto.cibil.EligibilityResultRequestDto;
import com.loanapp.loan_application.dto.cibil.EligibilityResultResponseDto;

public interface EligibilityResultService {

    EligibilityResultResponseDto checkEligibility(EligibilityResultRequestDto request);

    EligibilityResultResponseDto getLatestEligibility(Long customerId);
}