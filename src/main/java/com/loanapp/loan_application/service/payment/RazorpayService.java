package com.loanapp.loan_application.service.payment;

import java.math.BigDecimal;

public interface RazorpayService {

    String createOrder(BigDecimal amount, Long loanAccountId, Long emiScheduleId);
    boolean verifySignature(String orderId, String paymentId, String signature);
}