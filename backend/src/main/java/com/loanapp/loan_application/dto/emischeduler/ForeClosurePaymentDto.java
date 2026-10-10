package com.loanapp.loan_application.dto.emischeduler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForeClosurePaymentDto {

    private Long foreClosureId;

    private BigDecimal paidAmount;
}