package com.loanapp.loan_application.service.foreclosure;

import com.loanapp.loan_application.dto.emischeduler.ForeClosurePaymentDto;
import com.loanapp.loan_application.dto.foreclosure.ForeClosureApprovalDto;
import com.loanapp.loan_application.dto.foreclosure.ForeClosureCreateRequestDto;
import com.loanapp.loan_application.dto.foreclosure.ForeClosureResponseDto;

public interface ForeClosureService {

    ForeClosureResponseDto createRequest(ForeClosureCreateRequestDto request);

    ForeClosureResponseDto approveRequest(ForeClosureApprovalDto request);

    ForeClosureResponseDto rejectRequest(ForeClosureApprovalDto request);

    ForeClosureResponseDto makePayment(ForeClosurePaymentDto request);

    ForeClosureResponseDto getRequest(Long foreClosureId);
}