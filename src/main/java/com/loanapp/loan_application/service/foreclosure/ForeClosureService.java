package com.loanapp.loan_application.service.loanaccount;


import com.loanapp.loan_application.dto.emischeduler.ForeClosurePaymentDto;
import com.loanapp.loan_application.dto.foreclosure.ForeClosureResponseDto;
import com.loanapp.loan_application.dto.request.loanaccount.LoanAccountRequestDto;

public interface ForeClosureService {

    ForeClosureResponseDto createRequest(LoanAccountRequestDto.ForeClosureRequestDto request);

    ForeClosureResponseDto approveRequest(LoanAccountRequestDto.ForeClosureApprovalDto request);

    ForeClosureResponseDto rejectRequest(LoanAccountRequestDto.ForeClosureApprovalDto request);

    ForeClosureResponseDto makePayment(ForeClosurePaymentDto request);

    ForeClosureResponseDto getRequest(Long foreClosureId);
}