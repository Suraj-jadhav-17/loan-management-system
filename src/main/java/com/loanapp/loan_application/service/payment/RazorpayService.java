package com.loanapp.loan_application.service.payment;

import com.loanapp.loan_application.dto.payment.LoanPaymentsDto;

import java.math.BigDecimal;

public interface RazorpayService {

    String createOrder(BigDecimal amount, Long loanAccountId, Long emiScheduleId);
    boolean verifySignature(String orderId, String paymentId, String signature);

    LoanPaymentsDto verifyPayment(String orderId, String paymentId, String signature, LoanPaymentsDto dto);
}