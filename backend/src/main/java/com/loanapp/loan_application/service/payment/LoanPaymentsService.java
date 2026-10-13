package com.loanapp.loan_application.service.payment;

import com.loanapp.loan_application.dto.payment.LoanPaymentsDto;

import java.util.List;

public interface LoanPaymentsService {

    LoanPaymentsDto makePayment(LoanPaymentsDto dto);

    List<LoanPaymentsDto> getPayments(Long loanAccountId);
}