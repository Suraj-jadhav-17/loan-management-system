package com.loanapp.loan_application.dto.foreclosure;

import com.loanapp.loan_application.entity.foreclosure.ForeClosureType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForeClosureCreateRequestDto {

    private Long loanAccountId;

    private ForeClosureType foreClosureType;

    private BigDecimal partialAmount;
}