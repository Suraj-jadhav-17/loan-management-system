package com.loanapp.loan_application.dto.emischeduler;

import jakarta.persistence.Entity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
public class EmiSchedulesDto {

    private Long loanAccountId;
    private Long installmentNo;
    private LocalDate dueDate;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private BigDecimal emi;
    private PaymentStatus paymentStatus;
}