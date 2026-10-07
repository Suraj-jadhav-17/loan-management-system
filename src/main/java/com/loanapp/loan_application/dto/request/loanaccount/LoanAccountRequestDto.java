package com.loanapp.loan_application.dto.request.loanaccount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanAccountRequestDto {

    private Integer customerId;
    private Integer dealId;
    private String loanAccountNo;
    private BigDecimal loanAmount;
    private BigDecimal outstandingPrincipal;
    private String loanStatus;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private BigDecimal emiAmount;
    private LocalDateTime disbursementDate;
    private BigDecimal totalPaidAmount;
}