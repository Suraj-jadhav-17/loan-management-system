package com.loanapp.loan_application.dto.payment;

import com.loanapp.loan_application.entity.payment.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LoanPaymentsDto {
    private PaymentStatus paymentStatus;
    private Long paymentId;
    private Long loanAccountId;
    private BigDecimal paymentAmount;
    private LocalDateTime paymentDate;
    private String paymentName;
    private Long emiScheduleId;
}