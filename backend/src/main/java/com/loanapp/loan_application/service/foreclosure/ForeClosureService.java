package com.loanapp.loan_application.service.foreclosure;


import com.loanapp.loan_application.dto.emischeduler.ForeClosurePaymentDto;
import com.loanapp.loan_application.dto.foreclosure.ForeClosureResponseDto;


public interface ForeClosureService {

    ForeClosureResponseDto createRequest(LoanAccountRequest.ForeClosureRequestDto request);

    ForeClosureResponseDto approveRequest(LoanAccountRequest.ForeClosureApprovalDto request);

    ForeClosureResponseDto rejectRequest(LoanAccountRequest.ForeClosureApprovalDto request);

    ForeClosureResponseDto makePayment(ForeClosurePaymentDto request);

    ForeClosureResponseDto getRequest(Long foreClosureId);
}