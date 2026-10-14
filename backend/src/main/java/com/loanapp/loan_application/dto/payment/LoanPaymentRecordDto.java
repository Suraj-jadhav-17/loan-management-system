
        package com.loanapp.loan_application.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanPaymentRecordDto {

    private Long paymentId;

    private Long loanAccountId;

    private BigDecimal paymentAmount;

    private String paymentName;

    private String paymentStatus;
}

