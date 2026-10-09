package com.loanapp.loan_application.service.cibil;

import com.loanapp.loan_application.dto.cibil.EligibilityResultRequestDto;
import com.loanapp.loan_application.dto.cibil.EligibilityResultResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EligibilityResultService {

    EligibilityResultResponseDto checkEligibility(EligibilityResultRequestDto request);

    EligibilityResultResponseDto getLatestEligibility(Long customerId);

    Page<EligibilityResultResponseDto> getEligibilityHistory(Long customerId, Pageable pageable);
}
